package com.marcpicone.file_template_app.file_template_structure_preview_view

import kotlinx.coroutines.flow.StateFlow

interface FileTemplateStructurePreviewViewContract {

    val event: Event

    val uiState: UiState

    interface Event {

        fun onAttached()

        fun onDetached()
    }

    interface UiState {
        val structure: StateFlow<FileTemplateStructure?>
    }

    companion object {
        // @formatter:off
        const val FOLDER_ICON_PATH = "file_template_structure_preview_view/drawable/file_template_structure_preview_view_folder.svg"
        const val FILE_ICON_PATH = "file_template_structure_preview_view/drawable/file_template_structure_preview_view_file.svg"
        // @formatter:on
    }
}
