${ANDROID_PACKAGE}

class ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Presenter: ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract {

    private val innerState = createState()
    override val event: ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.Event = createEvent()
    override val uiState: ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.UiState = innerState

    private fun createEvent() = object : ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.Event {

        override fun onAttached() {}

        override fun onDetached() {}
    }

    private fun createState() = object : ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.UiState {
        // @formatter:off

        // @formatter:on
    }
}
