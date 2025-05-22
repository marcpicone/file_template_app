${ANDROID_PACKAGE}

interface ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract {

    interface UserAction {

        fun onAttachedToWindow()

        fun onDetachedFromWindow()
    }

    interface Screen
}
