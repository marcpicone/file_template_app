package com.marcpicone.file_template_app.main_view

import kotlinx.coroutines.flow.StateFlow

interface MainViewContract {

    val uiState: UiState
    val event: Event

    interface Event {

        fun onAttached()

        fun onDetached()

        fun onSelectDirectoryClicked()
    }

    interface UiState {

        val loading: StateFlow<Boolean>

        val pathToCurrentFolder: StateFlow<String?>

        val menuItems: List<MenuItem>
    }

    enum class MenuItem() {
        TEMPLATE()
    }
}
