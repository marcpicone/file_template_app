package com.marcpicone.file_template_app.file_template

import com.marcpicone.file_template_app.graph.FileTemplateAppGraph

class FileTemplateModule {

    fun createFileTemplateManager(): FileTemplateManager {
        return FileTemplateManagerImpl(
            FileTemplateAppGraph.getPathManager()
        )
    }
}
