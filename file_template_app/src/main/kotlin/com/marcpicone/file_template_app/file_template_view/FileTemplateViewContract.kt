package com.marcpicone.file_template_app.file_template_view

import kotlinx.coroutines.flow.StateFlow

interface FileTemplateViewContract {

    val uiState: UiState
    val event: Event

    interface Event {

        fun onAttached()

        fun onDetached()

        fun onMakeTemplateClicked()
    }

    interface UiState {

        val loaderVisible: StateFlow<Boolean>
    }
}
