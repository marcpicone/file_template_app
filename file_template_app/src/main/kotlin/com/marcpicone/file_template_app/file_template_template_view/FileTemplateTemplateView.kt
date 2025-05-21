package com.marcpicone.file_template_app.file_template_template_view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.marcpicone.file_template_app.graph.FileTemplateAppGraph

@Composable
fun FileTemplateTemplateView(
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
    uiState: FileTemplateTemplateViewContract.UiState,
    event: FileTemplateTemplateViewContract.Event,
    modifier: Modifier = Modifier
) {
    val templates = uiState.templates.collectAsState().value
    val currentGroup = uiState.currentGroup.collectAsState().value

    Row(modifier = modifier.padding(4.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(size = 20.dp)
                )
        ) {
            val groups = templates.flatMap { it.groups }.distinct()
            groups.forEach { group ->
                SelectableItem(
                    text = group,
                    selected = currentGroup == group,
                    onClicked = { event.onGroupClicked(group = group) },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(size = 20.dp)
                ),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = templates.filter { it.groups.contains(currentGroup) }) {
                val isSelected = uiState.currentTemplates.collectAsState().value.contains(it)
                SelectableItem(
                    text = it.id,
                    selected = isSelected,
                    onClicked = { event.onTemplateClicked(fileTemplate = it) },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun SelectableItem(
    text: String,
    selected: Boolean,
    onClicked: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClicked()
            }
            .background(
                color = if (!enabled) {
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                } else if (selected) {
                    MaterialTheme.colorScheme.onSecondary
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 2.dp,
                color = if (!enabled) {
                    MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
                } else if (selected) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSecondary
                },
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = text,
            color = if (!enabled) {
                MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
            } else if (selected) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onSecondary
            },
            fontWeight = FontWeight.Bold
        )
    }
}

private fun createPresenter(): FileTemplateTemplateViewContract {
    return FileTemplateTemplateViewPresenter(
        lazy { FileTemplateAppGraph.getFileTemplateFeatureViewManager() },
        lazy { FileTemplateAppGraph.getFileTemplateManager() },
        lazy { FileTemplateAppGraph.getFileTemplateTemplateViewManager() }
    )
}
