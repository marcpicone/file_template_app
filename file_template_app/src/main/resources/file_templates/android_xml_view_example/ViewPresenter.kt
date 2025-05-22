${ANDROID_PACKAGE}

class ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Presenter(
    private val screen: ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.Screen
) : ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.UserAction {

    override fun onAttachedToWindow() {
        updateScreen()
    }
    
    override fun onDetachedFromWindow() {}

    private fun updateScreen() {}
}
