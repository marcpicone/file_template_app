package com.marcpicone.file_template_app.feature

import java.util.UUID

class FeatureManagerImpl : FeatureManager {

    private val listeners = ArrayList<FeatureManager.Listener>()
    private val features = ArrayList<Feature>()

    override fun getFeatureIds(): List<String> {
        return features.map { it.id }
    }

    override fun getFeature(featureId: String): Feature? {
        return features.find { it.id == featureId }
    }

    override fun createNewFeature() {
        val feature = Feature(
            id = "feature_${UUID.randomUUID()}",
            name = "",
            attachedTemplateIds = emptyList()
        )
        features.add(feature)
        listeners.forEach { it.onChanged() }
    }

    override fun removeFeature(featureId: String) {
        val feature = getFeature(featureId) ?: throw IllegalStateException("Feature not found: $featureId")
        features.remove(feature)
        listeners.forEach { it.onChanged() }
    }

    override fun setFeatureName(featureId: String, newName: String) {
        val feature = getFeature(featureId) ?: throw IllegalStateException("Feature not found: $featureId")
        val featureUpdated = feature.copy(name = newName)
        setFeature(feature = feature, featureUpdated = featureUpdated)
    }

    override fun updateAttachedTemplates(featureId: String, templateIds: List<String>) {
        val feature = getFeature(featureId) ?: throw IllegalStateException("Feature not found: $featureId")
        val featureUpdated = feature.copy(attachedTemplateIds = templateIds)
        setFeature(feature = feature, featureUpdated = featureUpdated)
    }

    override fun addListener(listener: FeatureManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: FeatureManager.Listener) {
        listeners.remove(listener)
    }

    private fun setFeature(
        feature: Feature,
        featureUpdated: Feature
    ) {
        if (feature == featureUpdated) {
            return
        }
        val featureIndex = features.indexOf(feature)
        features.remove(feature)
        features.add(featureIndex, featureUpdated)
        listeners.forEach { it.onChanged() }
    }
}
