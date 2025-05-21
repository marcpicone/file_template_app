package com.marcpicone.file_template_app.file_template_template_view

import com.marcpicone.file_template_app.file_template.FileTemplate
import kotlinx.coroutines.flow.StateFlow

interface FileTemplateTemplateViewContract {

    val event: Event

    val uiState: UiState

    interface Event {

        fun onAttached()

        fun onDetached()

        fun onGroupClicked(group: String)

        fun onTemplateClicked(fileTemplate: FileTemplate.ParentFileTemplate)
    }

    interface UiState {

        val currentTemplates: StateFlow<List<FileTemplate.ParentFileTemplate>>

        val currentGroup: StateFlow<String>

        val templates: StateFlow<List<FileTemplate.ParentFileTemplate>>
    }
}
