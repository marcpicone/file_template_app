package com.marcpicone.file_template_app.file_template_feature_view

interface FileTemplateFeatureViewManager {

    fun setCurrentFeature(featureId: String)

    fun getCurrentFeatureId(): String?

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }
}
