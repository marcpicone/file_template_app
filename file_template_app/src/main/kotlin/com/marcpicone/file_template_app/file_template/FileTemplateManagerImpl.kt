package com.marcpicone.file_template_app.file_template

import androidx.annotation.WorkerThread
import com.marcpicone.file_template_app.path.PathManager
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import org.w3c.dom.Element

class FileTemplateManagerImpl(
    private val pathManager: PathManager
) : FileTemplateManager {

    private val listeners = ArrayList<FileTemplateManager.Listener>()
    private var initializeCall = false
    private var initialized = false
    private var fileTemplates: List<FileTemplate.ParentFileTemplate> = emptyList()
    private var templateFolderFile: File? = null

    override fun initialize() {
        if (initializeCall) {
            return
        }
        initializeCall = true
        templateFolderFile = pathManager.getTemplateFolderFile()
        pathManager.addListener(createPathListener())
        CoroutineScope(Dispatchers.IO).launch {
            loadTemplateIfPossible()
        }
    }

    override fun getInitialized(): Boolean {
        return initialized
    }

    override fun getFileTemplateIds(): List<String> {
        return fileTemplates.map { it.id }
    }

    override fun getFileTemplate(fileTemplateId: String): FileTemplate.ParentFileTemplate {
        return fileTemplates.first { it.id == fileTemplateId }
    }

    override fun addListener(listener: FileTemplateManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: FileTemplateManager.Listener) {
        listeners.remove(listener)
    }

    @WorkerThread
    private suspend fun loadTemplateIfPossible() {
        if (templateFolderFile == null) {
            return
        }
        val templates = loadTemplates()
        withContext(Dispatchers.Swing) {
            this@FileTemplateManagerImpl.fileTemplates = templates
            this@FileTemplateManagerImpl.initialized = true
            for (listener in listeners) {
                listener.onChanged()
            }
        }
    }

    private fun loadTemplates(): List<FileTemplate.ParentFileTemplate> {
        if (!templateFolderFile!!.exists()) {
            throw IllegalStateException("Template folder $templateFolderFile not found")
        }
        val templateXmls = templateFolderFile!!.listFiles()
            ?.filter { it.isDirectory }
            ?.mapNotNull { it?.listFiles()?.first { it.name == "template.xml" } }
            ?.map { File(it.path).readText() }

        if (templateXmls.isNullOrEmpty()) {
            throw IllegalStateException("No template.xml found in $templateFolderFile")
        }
        val fileTemplates = ArrayList<FileTemplate.ParentFileTemplate>()
        for (templateXml in templateXmls) {
            val document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(templateXml.byteInputStream())
            document.documentElement.normalize()

            val templateNodes = document.getElementsByTagName("template")
            fileTemplates += (0 until templateNodes.length)
                .map { templateNodes.item(it) }
                .filterIsInstance<Element>()
                .map { parseTemplate(it) }
        }

        return fileTemplates.sortedBy { it.parentFolderName }
    }

    private fun parseTemplate(element: Element): FileTemplate.ParentFileTemplate {
        val id = element.getAttribute("id")
        val parentFolderName = element.getAttribute("parent-folder-name")
        val name = element.getAttribute("file-name")
        val groups = element.getAttribute("groups")
            .takeIf { it.isNotBlank() }
            ?.split(",")
            ?.map { it.trim() }
            ?.toMutableList()
            ?.apply { add(0, "all") }
            ?: emptyList()
        val filePath = element.getAttribute("velocity-file-path")
        val fileExtension = element.getAttribute("file-extension")

        val childTemplates = element.childNodes
            .let { nodeList ->
                (0 until nodeList.length)
                    .map { nodeList.item(it) }
                    .filterIsInstance<Element>()
                    .filter { it.tagName == "child" }
                    .map { parseChildTemplateFile(it) }
            }

        return FileTemplate.ParentFileTemplate(
            id = id,
            parentFolderName = parentFolderName,
            name = name,
            groups = groups,
            filePath = filePath,
            fileExtension = fileExtension,
            child = childTemplates
        )
    }

    private fun parseChildTemplateFile(element: Element): FileTemplate.ChildFileTemplateFile {
        val name = element.getAttribute("file-name")
        val filePath = element.getAttribute("velocity-file-path")
        val fileExtension = element.getAttribute("file-extension")

        return FileTemplate.ChildFileTemplateFile(
            name = name,
            filePath = filePath,
            fileExtension = fileExtension,
        )
    }

    private fun createPathListener() = object : PathManager.Listener {
        override fun onChanged() {
            templateFolderFile = pathManager.getTemplateFolderFile()
            CoroutineScope(Dispatchers.IO).launch {
                loadTemplateIfPossible()
            }
        }
    }
}
