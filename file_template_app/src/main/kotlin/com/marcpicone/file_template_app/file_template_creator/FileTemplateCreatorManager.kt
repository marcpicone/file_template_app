package com.marcpicone.file_template_app.file_template_creator

interface FileTemplateCreatorManager {

    fun getStatus(): Status

    fun createFileTemplates()

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }

    enum class Status {
        IDLE,
        CREATING,
        CREATED
    }
}
