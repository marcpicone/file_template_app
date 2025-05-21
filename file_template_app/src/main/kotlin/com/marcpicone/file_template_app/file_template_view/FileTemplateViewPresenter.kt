package com.marcpicone.file_template_app.file_template_view

import com.marcpicone.file_template_app.file_template_creator.FileTemplateCreatorManager
import kotlin.system.exitProcess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FileTemplateViewPresenter(
    private val fileTemplateCreatorManager: Lazy<FileTemplateCreatorManager>
) : FileTemplateViewContract {

    private val innerState = createState()
    override val event: FileTemplateViewContract.Event = createEvent()
    override val uiState: FileTemplateViewContract.UiState = innerState
    private val fileTemplateCreatorListener = createFileTemplateCreatorListener()

    private fun createEvent() = object : FileTemplateViewContract.Event {

        override fun onAttached() {
            fileTemplateCreatorManager.value.addListener(fileTemplateCreatorListener)
            updateScreen()
        }

        override fun onDetached() {
            fileTemplateCreatorManager.value.removeListener(fileTemplateCreatorListener)
        }

        override fun onMakeTemplateClicked() {
            fileTemplateCreatorManager.value.createFileTemplates()
        }
    }

    private fun updateScreen() {
        updateLoaderVisible()
    }

    private fun updateLoaderVisible() {
        innerState.loaderVisible.update { createLoading() }
    }

    private fun createLoading(): Boolean {
        return fileTemplateCreatorManager.value.getStatus() == FileTemplateCreatorManager.Status.CREATING
    }

    private fun createState() = object : FileTemplateViewContract.UiState {
        // @formatter:off
        override val loaderVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
        // @formatter:on
    }

    private fun createFileTemplateCreatorListener() = object : FileTemplateCreatorManager.Listener {
        override fun onChanged() {
            updateLoaderVisible()
            if (fileTemplateCreatorManager.value.getStatus() == FileTemplateCreatorManager.Status.CREATED) {
                // auto close on file template created
                exitProcess(0)
            }
        }
    }
}
