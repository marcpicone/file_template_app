package com.marcpicone.file_template_app.feature

interface FeatureManager {

    fun getFeatureIds(): List<String>

    fun getFeature(featureId: String): Feature?

    fun createNewFeature()

    fun removeFeature(featureId: String)

    fun setFeatureName(featureId: String, newName: String)

    fun updateAttachedTemplates(featureId: String, templateIds: List<String>)

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }
}
