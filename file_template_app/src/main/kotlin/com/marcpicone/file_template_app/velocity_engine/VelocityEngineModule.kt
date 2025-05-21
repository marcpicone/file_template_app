package com.marcpicone.file_template_app.velocity_engine

import com.marcpicone.file_template_app.graph.FileTemplateAppGraph

class VelocityEngineModule {
    fun createVelocityEngineManager(): VelocityEngineManager {
        return VelocityEngineManagerImpl(
            FileTemplateAppGraph.getPathManager()
        )
    }
}
