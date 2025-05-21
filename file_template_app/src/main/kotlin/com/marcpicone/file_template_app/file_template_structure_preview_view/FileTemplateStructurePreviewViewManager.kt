package com.marcpicone.file_template_app.file_template_structure_preview_view

interface FileTemplateStructurePreviewViewManager {

    fun getStructure(): FileTemplateStructure?

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }
}
