package com.marcpicone.file_template_app.file_explorer

import java.io.File
import javax.swing.filechooser.FileFilter

interface FileExplorerManager {

    fun openFileExplorer(
        openPath: String,
        fileFilter: FileFilter,
        isMultiSelectionEnabled: Boolean,
        fileSelectionMode: FileSelectionMode
    )

    fun getSelectedFiles(): List<File>

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }

    enum class FileSelectionMode {
        FILES_ONLY,
        DIRECTORY_ONLY
    }
}
