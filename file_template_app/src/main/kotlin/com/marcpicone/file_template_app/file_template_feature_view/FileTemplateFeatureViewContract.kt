package com.marcpicone.file_template_app.file_template_feature_view

import com.marcpicone.file_template_app.feature.Feature
import kotlinx.coroutines.flow.StateFlow

interface FileTemplateFeatureViewContract {

    val event: Event

    val uiState: UiState

    interface Event {

        fun onAttached()

        fun onDetached()

        fun onAddFeatureClicked()

        fun onDeleteFeatureClicked(featureId: String)

        fun onFeatureNameChanged(newName: String)

        fun onFeatureFocused(featureId: String)
    }

    interface UiState {
        val features: StateFlow<List<Feature>>

        val currentFeatureId: StateFlow<String?>
    }
}
