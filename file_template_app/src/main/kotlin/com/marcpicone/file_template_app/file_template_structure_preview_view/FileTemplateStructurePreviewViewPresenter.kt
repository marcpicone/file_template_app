package com.marcpicone.file_template_app.file_template_structure_preview_view

import com.marcpicone.file_template_app.file_template_template_view.FileTemplateTemplateViewManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FileTemplateStructurePreviewViewPresenter(
    private val fileTemplateStructurePreviewViewManager: Lazy<FileTemplateStructurePreviewViewManager>,
    private val fileTemplateTemplateViewManager: Lazy<FileTemplateTemplateViewManager>
) : FileTemplateStructurePreviewViewContract {

    private val innerState = createState()
    override val event: FileTemplateStructurePreviewViewContract.Event = createEvent()
    override val uiState: FileTemplateStructurePreviewViewContract.UiState = innerState
    private val fileTemplateStructurePreviewViewListener = createFileTemplateStructurePreviewViewListener()
    private val fileTemplateTemplateViewListener = createFileTemplateTemplateViewListener()

    private fun createEvent() = object : FileTemplateStructurePreviewViewContract.Event {

        override fun onAttached() {
            fileTemplateStructurePreviewViewManager.value.addListener(fileTemplateStructurePreviewViewListener)
            fileTemplateTemplateViewManager.value.addListener(fileTemplateTemplateViewListener)
            updateScreen()
        }

        override fun onDetached() {
            fileTemplateStructurePreviewViewManager.value.removeListener(fileTemplateStructurePreviewViewListener)
            fileTemplateTemplateViewManager.value.removeListener(fileTemplateTemplateViewListener)
        }
    }

    private fun updateScreen() {
        updateStructure()
    }

    private fun updateStructure() {
        innerState.structure.update { createStructure() }
    }

    private fun createStructure(): FileTemplateStructure? {
        return fileTemplateStructurePreviewViewManager.value.getStructure()
    }

    private fun createState() = object : FileTemplateStructurePreviewViewContract.UiState {
        // @formatter:off
        override val structure = MutableStateFlow<FileTemplateStructure?>(null)
        // @formatter:on
    }

    private fun createFileTemplateTemplateViewListener() = object : FileTemplateTemplateViewManager.Listener {
        override fun onChanged() {
            updateStructure()
        }
    }

    private fun createFileTemplateStructurePreviewViewListener() = object : FileTemplateStructurePreviewViewManager.Listener {
        override fun onChanged() {
            updateStructure()
        }
    }
}
