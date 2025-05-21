${ANDROID_PACKAGE}

interface ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract {

    val event: Event

    val uiState: UiState

    interface Event {

        fun onAttached()

        fun onDetached()
    }

    interface UiState
}
