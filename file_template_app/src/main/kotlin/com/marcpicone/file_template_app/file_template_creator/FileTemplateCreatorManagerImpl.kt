package com.marcpicone.file_template_app.file_template_creator

import androidx.annotation.WorkerThread
import com.marcpicone.file_template_app.feature.FeatureManager
import com.marcpicone.file_template_app.file_template.FileTemplate
import com.marcpicone.file_template_app.file_template.FileTemplateManager
import com.marcpicone.file_template_app.file_template_creator.FileTemplateCreatorManager.Listener
import com.marcpicone.file_template_app.file_template_creator.FileTemplateCreatorManager.Status
import com.marcpicone.file_template_app.path.PathManager
import com.marcpicone.file_template_app.velocity_engine.VelocityEngineManager
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine

class FileTemplateCreatorManagerImpl(
    private val featureManager: FeatureManager,
    private val fileTemplateManager: FileTemplateManager,
    private val pathManager: PathManager,
    private val velocityEngineManager: VelocityEngineManager
) : FileTemplateCreatorManager {

    private val listeners = ArrayList<Listener>()
    private var status = Status.IDLE
    private val workerScope = CoroutineScope(Dispatchers.IO)

    override fun getStatus(): Status {
        return status
    }

    override fun createFileTemplates() {
        if (status == Status.CREATING) {
            return
        }
        setStatus(Status.CREATING)

        val featureIds = featureManager.getFeatureIds()
        if (featureIds.isEmpty()) {
            setStatus(Status.CREATED)
            return
        }

        val velocityEngine = velocityEngineManager.getEngine()
        workerScope.launch {
            val jobs = featureIds.map { featureId ->
                val feature = featureManager.getFeature(featureId)
                    ?: throw IllegalStateException("No current feature for featureId $featureId")
                val featureName = feature.name
                val fileTemplates = feature.attachedTemplateIds.map { fileTemplateManager.getFileTemplate(it) }

                async {
                    fileTemplates.forEach { fileTemplate ->
                        val velocityContext = velocityEngineManager.getEngineContext(
                            fileTemplate = fileTemplate,
                            featureName = featureName.takeIf { it.trim().isNotEmpty() }
                        )
                        createParentTemplate(
                            velocityEngine = velocityEngine,
                            velocityContext = velocityContext,
                            fileTemplate = fileTemplate
                        )
                        val child = fileTemplate.child
                        for (i in 0 until child.size) {
                            createChildFileTemplate(
                                childTemplate = child[i],
                                velocityContext = velocityContext,
                                velocityEngine = velocityEngine,
                                parentFolderName = fileTemplate.parentFolderName
                            )
                        }
                    }
                }
            }
            jobs.awaitAll()
            withContext(Dispatchers.Swing) {
                setStatus(Status.CREATED)
            }
        }
    }

    override fun addListener(listener: Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun setStatus(status: Status) {
        if (this.status == status) {
            return
        }
        this.status = status
        listeners.forEach { it.onChanged() }
    }

    @WorkerThread
    private fun createParentTemplate(
        velocityEngine: VelocityEngine,
        velocityContext: VelocityContext,
        fileTemplate: FileTemplate.ParentFileTemplate
    ) {
        val templateName = "file_templates/${fileTemplate.parentFolderName}/${fileTemplate.name}"
        val templateFile = velocityEngine.getTemplate(templateName)
        val templateWriter = StringWriter()
        val fileNameWriter = StringWriter()
        val fileExtension = fileTemplate.fileExtension
        val filePath = fileTemplate.filePath
        val pathToCurrentFolder = pathManager.getPathToCurrentFolder()

        velocityEngine.evaluate(
            velocityContext,
            fileNameWriter,
            "filePath",
            filePath
        )
        templateFile.merge(velocityContext, templateWriter)

        val resolvedFileName = fileNameWriter.toString().trim()
        val outputPath = Paths.get("$pathToCurrentFolder/$resolvedFileName.$fileExtension")

        Files.createDirectories(outputPath.parent)
        Files.write(outputPath, templateWriter.toString().toByteArray())
    }

    @WorkerThread
    private fun createChildFileTemplate(
        childTemplate: FileTemplate.ChildFileTemplateFile,
        velocityContext: VelocityContext,
        velocityEngine: VelocityEngine,
        parentFolderName: String
    ) {
        val templateName = "file_templates/$parentFolderName/${childTemplate.name}"
        val templateFile = velocityEngine.getTemplate(templateName)
        val templateWriter = StringWriter()
        val fileNameWriter = StringWriter()
        val fileExtension = childTemplate.fileExtension

        velocityEngine.evaluate(
            velocityContext,
            fileNameWriter,
            "filePath",
            childTemplate.filePath
        )

        templateFile.merge(velocityContext, templateWriter)

        val resolvedFileName = fileNameWriter.toString().trim()
        val outputPath = Paths.get(
            "${pathManager.getPathToCurrentFolder()}/$resolvedFileName.$fileExtension"
        )

        Files.createDirectories(outputPath.parent)
        Files.write(outputPath, templateWriter.toString().toByteArray())
    }
}
