package com.marcpicone.file_template_app.file_template_feature_view

import com.marcpicone.file_template_app.feature.Feature
import com.marcpicone.file_template_app.feature.FeatureManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FileTemplateFeatureViewPresenter(
    private val featureManager: Lazy<FeatureManager>,
    private val fileTemplateFeatureViewManager: Lazy<FileTemplateFeatureViewManager>
) : FileTemplateFeatureViewContract {

    private val innerState = createState()
    override val event: FileTemplateFeatureViewContract.Event = createEvent()
    override val uiState: FileTemplateFeatureViewContract.UiState = innerState
    private val featureListener = createFeatureListener()
    private val fileTemplateFeatureViewListener = createFileTemplateFeatureViewListener()

    private fun createEvent() = object : FileTemplateFeatureViewContract.Event {

        override fun onAttached() {
            featureManager.value.addListener(featureListener)
            fileTemplateFeatureViewManager.value.addListener(fileTemplateFeatureViewListener)
            updateScreen()
        }

        override fun onDetached() {
            featureManager.value.removeListener(featureListener)
            fileTemplateFeatureViewManager.value.removeListener(fileTemplateFeatureViewListener)
        }

        override fun onAddFeatureClicked() {
            featureManager.value.createNewFeature()
        }

        override fun onDeleteFeatureClicked(featureId: String) {
            featureManager.value.removeFeature(featureId = featureId)
        }

        override fun onFeatureNameChanged(newName: String) {
            featureManager.value.setFeatureName(
                featureId = fileTemplateFeatureViewManager.value.getCurrentFeatureId()!!,
                newName = newName
            )
        }

        override fun onFeatureFocused(featureId: String) {
            fileTemplateFeatureViewManager.value.setCurrentFeature(featureId = featureId)
        }
    }

    private fun updateScreen() {
        updateFeatures()
        updateCurrentFeature()
    }

    private fun updateFeatures() {
        innerState.features.update { createFeatures() }
    }

    private fun updateCurrentFeature() {
        innerState.currentFeatureId.update { createCurrentFeature() }
    }

    private fun createFeatures(): List<Feature> {
        return featureManager.value.getFeatureIds().mapNotNull { featureId ->
            featureManager.value.getFeature(featureId) ?: return@mapNotNull null
        }
    }

    private fun createCurrentFeature(): String? {
        return fileTemplateFeatureViewManager.value.getCurrentFeatureId()
    }

    private fun createState() = object : FileTemplateFeatureViewContract.UiState {
        // @formatter:off
        override val features: MutableStateFlow<List<Feature>> = MutableStateFlow(emptyList())
        override val currentFeatureId: MutableStateFlow<String?> = MutableStateFlow(null)
        // @formatter:on
    }

    private fun createFeatureListener() = object : FeatureManager.Listener {
        override fun onChanged() {
            updateFeatures()
        }
    }

    private fun createFileTemplateFeatureViewListener() = object : FileTemplateFeatureViewManager.Listener {
        override fun onChanged() {
            updateFeatures()
            updateCurrentFeature()
        }
    }
}
