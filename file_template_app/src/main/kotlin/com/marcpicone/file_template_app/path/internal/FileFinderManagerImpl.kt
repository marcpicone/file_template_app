package com.marcpicone.file_template_app.path.internal

import androidx.annotation.WorkerThread
import com.marcpicone.file_template_app.path.PathManager
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext

class FileFinderManagerImpl(
    private val pathManager: PathManager
) : FileFinderManager {

    private val listeners = ArrayList<FileFinderManager.Listener>()
    private val appRepresentation = ArrayList<AppDetail>()
    private val flatRepresentation = HashMap<String, AppDetail>()
    private var initializeCall = false
    private var initialized = false

    override fun initialize() {
        if (initializeCall) {
            return
        }
        initializeCall = true
        val rootPath = pathManager.getRootPath()
        if (rootPath == null) {
            pathManager.addListener(createPathListener())
            return
        }
        createRepresentations()
    }

    override fun getInitialized(): Boolean {
        return initialized
    }

    override fun getFilePath(fileName: String): String? {
        if (!initializeCall) {
            throw IllegalStateException("Please call initialize first")
        }
        return flatRepresentation[fileName]?.path
    }

    override fun addListener(listener: FileFinderManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: FileFinderManager.Listener) {
        listeners.remove(listener)
    }

    @WorkerThread
    private fun createAppRepresentation(directory: File): List<AppDetail> {
        return directory.listFiles()?.mapNotNull { file ->
            if (file.isDirectory) {
                AppDetail.AppFolder(
                    name = file.name,
                    path = file.absolutePath,
                    details = createAppRepresentation(file)
                )
            } else {
                AppDetail.AppFile(
                    name = file.name,
                    path = file.absolutePath
                )
            }
        } ?: emptyList()
    }

    private fun createFlatRepresentation(details: List<AppDetail>): Map<String, AppDetail> {
        val result = mutableMapOf<String, AppDetail>()
        for (detail in details) {
            result[detail.name] = detail
            if (detail is AppDetail.AppFolder) {
                result.putAll(createFlatRepresentation(detail.details))
            }
        }
        return result
    }

    private fun createRepresentations() {
        val rootPath = pathManager.getRootPath()!!
        val rootFile = File(rootPath)
        if (!rootFile.exists()) {
            throw IllegalStateException("Root File do not exist for path $rootPath")
        }
        CoroutineScope(Dispatchers.IO).launch {
            val representation = createAppRepresentation(rootFile)
            val flat = createFlatRepresentation(representation)
            withContext(Dispatchers.Swing) {
                appRepresentation.clear()
                appRepresentation.addAll(representation)
                flatRepresentation.clear()
                flatRepresentation.putAll(flat)
                initialized = true
                listeners.forEach { it.onChanged() }
            }
        }
    }

    private fun createPathListener() = object : PathManager.Listener {
        override fun onChanged() {
            if (pathManager.getInitialized()) {
                return
            }
            createRepresentations()
        }
    }

    private sealed class AppDetail(open val name: String, open val path: String) {

        data class AppFolder(
            override val path: String,
            override val name: String,
            val details: List<AppDetail>
        ) : AppDetail(
            name = name,
            path = path
        )

        data class AppFile(
            override val path: String,
            override val name: String
        ) : AppDetail(
            name = name,
            path = path
        )
    }
}
