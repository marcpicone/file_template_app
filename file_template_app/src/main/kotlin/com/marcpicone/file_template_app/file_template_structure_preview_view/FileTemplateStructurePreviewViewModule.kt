package com.marcpicone.file_template_app.file_template_structure_preview_view

import com.marcpicone.file_template_app.graph.FileTemplateAppGraph

class FileTemplateStructurePreviewViewModule {
    fun createFileTemplateStructurePreviewViewManager(): FileTemplateStructurePreviewViewManager {
        return FileTemplateStructurePreviewViewManagerImpl(
            FileTemplateAppGraph.getFeatureManager(),
            FileTemplateAppGraph.getFileTemplateFeatureViewManager(),
            FileTemplateAppGraph.getFileTemplateManager(),
            FileTemplateAppGraph.getPathManager(),
            FileTemplateAppGraph.getVelocityEngineManager()
        )
    }
}
