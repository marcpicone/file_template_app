package com.marcpicone.file_template_app.file_template_template_view

import com.marcpicone.file_template_app.feature.FeatureManager
import com.marcpicone.file_template_app.file_template_feature_view.FileTemplateFeatureViewManager
import kotlin.collections.get

class FileTemplateTemplateViewManagerImpl(
    private val featureManager: FeatureManager,
    private val fileTemplateFeatureViewManager: Lazy<FileTemplateFeatureViewManager>
) : FileTemplateTemplateViewManager {

    private val listeners = ArrayList<FileTemplateTemplateViewManager.Listener>()
    private var group: String? = null
    private var featureIdToTemplateIds = HashMap<String, List<String>>()

    override fun setGroup(group: String) {
        if (this.group == group) {
            return
        }
        this.group = group
        listeners.forEach { it.onChanged() }
    }

    override fun toggleTemplateId(templateId: String) {
        val currentFeatureId = fileTemplateFeatureViewManager.value.getCurrentFeatureId() ?: throw IllegalStateException("No feature selected")
        val templateIds = featureIdToTemplateIds[currentFeatureId]
        if (templateIds?.contains(templateId) == true) {
            val newTemplateIds = templateIds
                .toMutableList()
                .apply { remove(templateId) }
                .toList()
            featureIdToTemplateIds[currentFeatureId] = newTemplateIds
        } else {
            val newTemplateIds = templateIds?.toMutableList()?.apply {
                add(templateId)
            }?.toList() ?: listOf(templateId)
            featureIdToTemplateIds[currentFeatureId] = newTemplateIds
        }
        featureManager.updateAttachedTemplates(
            featureId = currentFeatureId,
            templateIds = featureIdToTemplateIds[currentFeatureId] ?: emptyList()
        )
        listeners.forEach { it.onChanged() }
    }

    override fun getGroup(): String? {
        return group
    }

    override fun getTemplateIds(featureId: String): List<String> {
        return featureIdToTemplateIds[featureId] ?: emptyList()
    }

    override fun getCurrentFeatureTemplateIds(): List<String> {
        return featureIdToTemplateIds[fileTemplateFeatureViewManager.value.getCurrentFeatureId()] ?: emptyList()
    }

    override fun addListener(listener: FileTemplateTemplateViewManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: FileTemplateTemplateViewManager.Listener) {
        listeners.remove(listener)
    }
}
