package com.marcpicone.file_template_app.file_template_view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.marcpicone.file_template_app.file_template_feature_view.FileTemplateFeatureView
import com.marcpicone.file_template_app.file_template_structure_preview_view.FileTemplateStructurePreviewView
import com.marcpicone.file_template_app.file_template_template_view.FileTemplateTemplateView
import com.marcpicone.file_template_app.graph.FileTemplateAppGraph

@Composable
fun FileTemplateView(
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
    uiState: FileTemplateViewContract.UiState,
    event: FileTemplateViewContract.Event,
    modifier: Modifier = Modifier
) {
    val loaderVisible = uiState.loaderVisible.collectAsState().value
    val buttonFocusRequester = remember { FocusRequester() }

    if (loaderVisible) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.size(100.dp).padding(16.dp).align(alignment = Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    } else {
        Row(modifier = modifier) {
            FileTemplateFeatureView(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FileTemplateTemplateView(modifier = Modifier.weight(3f))
                TextButton(
                    // enabled = featureNameError.not(),
                    onClick = {
                        event.onMakeTemplateClicked()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .focusRequester(buttonFocusRequester)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Create Template",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            FileTemplateStructurePreviewView(modifier = Modifier.weight(1f))
        }
    }
}

private fun createPresenter(): FileTemplateViewContract {
    return FileTemplateViewPresenter(
        lazy { FileTemplateAppGraph.getFileTemplateCreatorManager() }
    )
}
