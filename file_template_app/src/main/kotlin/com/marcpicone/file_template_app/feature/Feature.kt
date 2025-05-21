package com.marcpicone.file_template_app.feature

data class Feature(
    val id: String,
    val name: String,
    val attachedTemplateIds: List<String>
)
