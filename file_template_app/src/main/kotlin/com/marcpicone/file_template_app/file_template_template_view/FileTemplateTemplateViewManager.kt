package com.marcpicone.file_template_app.file_template_template_view

interface FileTemplateTemplateViewManager {

    /**
     * @throws IllegalArgumentException if templateId is not found
     */
    fun toggleTemplateId(templateId: String)

    fun getTemplateIds(featureId: String): List<String>

    fun getCurrentFeatureTemplateIds(): List<String>

    fun setGroup(group: String)

    fun getGroup(): String?

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }
}
