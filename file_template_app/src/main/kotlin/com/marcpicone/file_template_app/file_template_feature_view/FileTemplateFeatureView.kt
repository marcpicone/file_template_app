package com.marcpicone.file_template_app.file_template_feature_view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.marcpicone.file_template_app.feature.Feature
import com.marcpicone.file_template_app.graph.FileTemplateAppGraph

@Composable
fun FileTemplateFeatureView(
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
    uiState: FileTemplateFeatureViewContract.UiState,
    event: FileTemplateFeatureViewContract.Event,
    modifier: Modifier = Modifier
) {
    val features = uiState.features.collectAsState().value
    val currentFeatureId = uiState.currentFeatureId.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxHeight()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(size = 20.dp)
            )
    ) {
        AddFeature(
            onClicked = {
                event.onAddFeatureClicked()
            }
        )
        LazyColumn {
            features.forEachIndexed { index, feature ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .clickable {
                                event.onFeatureFocused(featureId = feature.id)
                            }
                            .background(
                                color = if (currentFeatureId == feature.id) {
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                } else {
                                    Color.Transparent
                                }
                            )
                    ) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(color = MaterialTheme.colorScheme.onPrimary)
                        )
                        FeatureName(
                            index = index,
                            feature = feature,
                            onFeatureNameChanged = event::onFeatureNameChanged,
                            onFeatureFocused = event::onFeatureFocused,
                            onFeatureDeleted = event::onDeleteFeatureClicked
                        )
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(color = MaterialTheme.colorScheme.onPrimary)
                        )
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(color = MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureName(
    index: Int,
    feature: Feature,
    onFeatureNameChanged: (newName: String) -> Unit,
    onFeatureFocused: (featureId: String) -> Unit,
    onFeatureDeleted: (featureId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Feature ${index + 1}",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(
                onClick = { onFeatureDeleted(feature.id) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Feature",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        OutlinedTextField(
            value = feature.name,
            onValueChange = {
                onFeatureNameChanged(it)
            },
            label = { Text("Feature Name in snake_case, no special characters, no spaces") },
            isError = feature.name.isNameError(),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = if (feature.name.isNameError()) Color(0xffEC5F6C) else MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                disabledIndicatorColor = Color.Transparent,
                errorCursorColor = Color(0xffEC5F6C),
                errorIndicatorColor = Color.Transparent,
                errorLabelColor = Color(0xffEC5F6C),
                focusedIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(size = 20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .onFocusChanged {
                    if (it.isFocused) {
                        onFeatureFocused(feature.id)
                    }
                }
        )
    }
}

private fun String.isNameError(): Boolean {
    return this.contains(Regex("[^a-z0-9_]"))
}

@Composable
private fun AddFeature(
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClicked()
            }
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp)
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Feature",
            modifier = Modifier.size(34.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Add Feature",
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun createPresenter(): FileTemplateFeatureViewContract {
    return FileTemplateFeatureViewPresenter(
        lazy { FileTemplateAppGraph.getFeatureManager() },
        lazy { FileTemplateAppGraph.getFileTemplateFeatureViewManager() }
    )
}
