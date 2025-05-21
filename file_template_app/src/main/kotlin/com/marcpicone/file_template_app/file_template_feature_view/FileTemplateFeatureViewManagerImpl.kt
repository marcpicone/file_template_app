package com.marcpicone.file_template_app.file_template_feature_view

class FileTemplateFeatureViewManagerImpl() : FileTemplateFeatureViewManager {

    private val listeners = ArrayList<FileTemplateFeatureViewManager.Listener>()
    private var currentFeatureId: String? = null

    override fun setCurrentFeature(featureId: String) {
        if (this.currentFeatureId == featureId) {
            return
        }
        this.currentFeatureId = featureId
        listeners.forEach { it.onChanged() }
    }

    override fun getCurrentFeatureId(): String? {
        return currentFeatureId
    }

    override fun addListener(listener: FileTemplateFeatureViewManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: FileTemplateFeatureViewManager.Listener) {
        listeners.remove(listener)
    }
}
