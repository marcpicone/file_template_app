package com.marcpicone.file_template_app.main_view

import com.marcpicone.file_template_app.file_explorer.FileExplorerManager
import com.marcpicone.file_template_app.launch_load.LaunchLoadManager
import com.marcpicone.file_template_app.path.PathManager
import java.io.File
import javax.swing.filechooser.FileFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainViewPresenter(
    private val launchLoadManager: LaunchLoadManager,
    private val fileExplorerManager: Lazy<FileExplorerManager>,
    private val pathManager: Lazy<PathManager>
) : MainViewContract {

    private val innerState = createState()
    override val uiState: MainViewContract.UiState = innerState
    override val event: MainViewContract.Event = createEvent()
    private val launchLoadListener = createLaunchLoadListener()
    private val fileExplorerListener = createFileExplorerListener()
    private val pathListener = createPathListener()

    private fun createEvent() = object : MainViewContract.Event {
        override fun onAttached() {
            launchLoadManager.addListener(launchLoadListener)
            fileExplorerManager.value.addListener(fileExplorerListener)
            pathManager.value.addListener(pathListener)
            updateScreen()
        }

        override fun onDetached() {
            launchLoadManager.removeListener(launchLoadListener)
            fileExplorerManager.value.removeListener(fileExplorerListener)
            pathManager.value.removeListener(pathListener)
        }

        override fun onSelectDirectoryClicked() {
            val rootPath = System.getProperty("user.dir")
            fileExplorerManager.value.openFileExplorer(
                openPath = rootPath,
                fileFilter = createFileFilter(),
                isMultiSelectionEnabled = false,
                fileSelectionMode = FileExplorerManager.FileSelectionMode.DIRECTORY_ONLY,
            )
        }
    }

    private fun createFileFilter(): FileFilter = object : FileFilter() {
        override fun accept(file: File): Boolean {
            return file.isDirectory
        }

        override fun getDescription(): String {
            return "Directory only"
        }
    }

    private fun updateScreen() {
        updateLoading()
        updatePathToCurrentFolder()
    }

    private fun updateLoading() {
        innerState.loading.update { createLoading() }
    }

    private fun updatePathToCurrentFolder() {
        innerState.pathToCurrentFolder.update { createPathToCurrentFolder() }
    }

    private fun createLoading(): Boolean {
        return launchLoadManager.getStatus() != LaunchLoadManager.Status.LOADED
    }

    private fun createPathToCurrentFolder(): String? {
        return pathManager.value.getPathToCurrentFolder()
    }

    private fun createLaunchLoadListener() = object : LaunchLoadManager.Listener {
        override fun onChanged() {
            updateLoading()
        }
    }

    private fun createState() = object : MainViewContract.UiState {
        // @formatter:off
        override val loading: MutableStateFlow<Boolean> = MutableStateFlow(true)
        override val menuItems: List<MainViewContract.MenuItem> = MainViewContract.MenuItem.entries
        override val pathToCurrentFolder: MutableStateFlow<String?> = MutableStateFlow(null)
        // @formatter:on
    }

    private fun createFileExplorerListener() = object : FileExplorerManager.Listener {
        override fun onChanged() {
            if (fileExplorerManager.value.getSelectedFiles().isNotEmpty()) {
                val path = fileExplorerManager.value.getSelectedFiles().first().path
                pathManager.value.setPathToCurrentFolder(path = path)
            }
        }
    }

    private fun createPathListener() = object : PathManager.Listener {
        override fun onChanged() {
            updatePathToCurrentFolder()
        }
    }
}
