package com.marcpicone.file_template_app.file_template_creator

import com.marcpicone.file_template_app.graph.FileTemplateAppGraph

class FileTemplateCreatorModule {

    fun createFileTemplateCreatorManager(): FileTemplateCreatorManager {
        return FileTemplateCreatorManagerImpl(
            FileTemplateAppGraph.getFeatureManager(),
            FileTemplateAppGraph.getFileTemplateManager(),
            FileTemplateAppGraph.getPathManager(),
            FileTemplateAppGraph.getVelocityEngineManager()
        )
    }
}
