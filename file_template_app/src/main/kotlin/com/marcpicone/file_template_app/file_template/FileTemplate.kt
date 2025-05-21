package com.marcpicone.file_template_app.file_template

sealed interface FileTemplate {
    val name: String
    val filePath: String
    val fileExtension: String

    data class ParentFileTemplate(
        override val name: String,
        override val filePath: String,
        override val fileExtension: String,
        val groups: List<String>,
        val parentFolderName: String,
        val id: String,
        val child: List<ChildFileTemplateFile>
    ) : FileTemplate

    data class ChildFileTemplateFile(
        override val name: String,
        override val filePath: String,
        override val fileExtension: String
    ) : FileTemplate
}
