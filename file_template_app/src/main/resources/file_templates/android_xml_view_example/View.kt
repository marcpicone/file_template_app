${ANDROID_PACKAGE}

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IdRes
import ${ANDROID_PACKAGE_NAME}.R

class ${FEATURE_NAME_TO_UPPER_CAMEL_CASE} @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val view = inflate(context, R.layout.${FEATURE_NAME_TO_LOWER_SNAKE_CASE}, this)
    private val userAction by lazy { createUserAction() }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        userAction.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        userAction.onDetachedFromWindow()
    }

    private fun createScreen() = object : ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.Screen {
    }

    private fun createUserAction(): ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.UserAction {
        if (isInEditMode) {
            return object : ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.UserAction {
                override fun onAttachedToWindow() {}
                override fun onDetachedFromWindow() {}
            }
        }
        return ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Presenter(
            createScreen()
        )
    }
}
