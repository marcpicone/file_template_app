package com.marcpicone.file_template_app.launch_load

import com.marcpicone.file_template_app.graph.FileTemplateAppGraph

class LaunchLoadModule {
    fun createLaunchLoadManager(): LaunchLoadManager {
        return LaunchLoadManagerImpl(
            FileTemplateAppGraph.getPathManager(),
            FileTemplateAppGraph.getFileTemplateManager(),
            FileTemplateAppGraph.getVelocityEngineManager()
        )
    }
}
