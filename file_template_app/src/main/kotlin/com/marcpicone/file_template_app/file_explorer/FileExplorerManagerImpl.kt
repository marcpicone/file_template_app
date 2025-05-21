package com.marcpicone.file_template_app.file_explorer

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

class FileExplorerManagerImpl : FileExplorerManager {

    private val listeners = ArrayList<FileExplorerManager.Listener>()
    private val fileChooser = JFileChooser()
    private var selectedFiles: List<File> = emptyList()

    override fun openFileExplorer(
        openPath: String,
        fileFilter: FileFilter,
        isMultiSelectionEnabled: Boolean,
        fileSelectionMode: FileExplorerManager.FileSelectionMode
    ) {
        fileChooser.currentDirectory = File(openPath)
        fileChooser.fileFilter = fileFilter
        fileChooser.isMultiSelectionEnabled = isMultiSelectionEnabled
        fileChooser.fileSelectionMode = when (fileSelectionMode) {
            FileExplorerManager.FileSelectionMode.FILES_ONLY -> JFileChooser.FILES_ONLY
            FileExplorerManager.FileSelectionMode.DIRECTORY_ONLY -> JFileChooser.DIRECTORIES_ONLY
        }

        val approveButtonText = when (fileSelectionMode) {
            FileExplorerManager.FileSelectionMode.FILES_ONLY -> "Select Files"
            FileExplorerManager.FileSelectionMode.DIRECTORY_ONLY -> "Select Directory"
        }

        val result = fileChooser.showDialog(
            /* parent = */ null,
            /* approveButtonText = */ approveButtonText
        )
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFiles: Array<File> = if (isMultiSelectionEnabled) {
                fileChooser.selectedFiles
            } else {
                arrayOf(fileChooser.selectedFile)
            }
            this.selectedFiles = selectedFiles.toList()
            listeners.forEach { it.onChanged() }
        }
    }

    override fun getSelectedFiles(): List<File> {
        return selectedFiles
    }

    override fun addListener(listener: FileExplorerManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: FileExplorerManager.Listener) {
        listeners.remove(listener)
    }
}
