package com.marcpicone.file_template_app.file_template_structure_preview_view

sealed class FileTemplateStructure(open val name: String, open val path: String) {

    data class FileTemplateStructureFolder(
        override val path: String,
        override val name: String,
        val details: List<FileTemplateStructure>
    ) : FileTemplateStructure(
        name = name,
        path = path
    )

    data class FileTemplateStructureFile(
        override val path: String,
        override val name: String
    ) : FileTemplateStructure(
        name = name,
        path = path
    )
}
