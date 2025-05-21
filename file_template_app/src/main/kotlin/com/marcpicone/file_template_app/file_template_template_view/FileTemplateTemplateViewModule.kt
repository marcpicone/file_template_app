package com.marcpicone.file_template_app.file_template_template_view

import com.marcpicone.file_template_app.graph.FileTemplateAppGraph

class FileTemplateTemplateViewModule {
    fun createFileTemplateTemplateViewManager(): FileTemplateTemplateViewManager {
        return FileTemplateTemplateViewManagerImpl(
            FileTemplateAppGraph.getFeatureManager(),
            lazy { FileTemplateAppGraph.getFileTemplateFeatureViewManager() }
        )
    }
}
