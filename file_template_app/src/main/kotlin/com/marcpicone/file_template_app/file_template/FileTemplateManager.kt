package com.marcpicone.file_template_app.file_template

interface FileTemplateManager {

    fun initialize()

    fun getInitialized(): Boolean

    fun getFileTemplateIds(): List<String>

    fun getFileTemplate(fileTemplateId: String): FileTemplate.ParentFileTemplate

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }
}
