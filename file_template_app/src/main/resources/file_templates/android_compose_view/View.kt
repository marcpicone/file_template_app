${ANDROID_PACKAGE}

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}(
    modifier: Modifier = Modifier
) {
    val presenter = createPresenter()
    val uiState = presenter.uiState
    val event = presenter.event

    LaunchedEffect(Unit) { event.onAttached() }
    DisposableEffect(Unit) { onDispose { event.onDetached() } }

    Content(uiState = uiState, event = event, modifier = modifier)
}

@Composable
private fun Content(
    uiState: ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.UiState,
    event: ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.Event,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun createPresenter(): ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract {
    if (LocalInspectionMode.current) {
        return object : ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract {
            override val event: ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.Event
            get() = object : ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.Event {
                override fun onAttached() {}
                override fun onDetached() {}
            }
            override val uiState: ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.UiState
            get() = object : ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Contract.UiState {}
        }
    }
    return ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Presenter()
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF101010
)
@Composable
fun ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}Preview() {
    ${FEATURE_NAME_TO_UPPER_CAMEL_CASE}()
}
