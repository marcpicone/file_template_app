package com.marcpicone.file_template_app.path

import com.marcpicone.file_template_app.graph.FileTemplateAppGraph
import com.marcpicone.file_template_app.path.internal.FileFinderManager
import com.marcpicone.file_template_app.path.internal.FileFinderManagerImpl

class PathModule {

    fun createPathManager(): PathManager {
        return PathManagerImpl(
            createFileFinderManager(),
            FileTemplateAppGraph.getPathToCurrentFolder(),
            FileTemplateAppGraph.getPackageName()
        )
    }

    private fun createFileFinderManager(): Lazy<FileFinderManager> {
        return lazy {
            FileFinderManagerImpl(
                FileTemplateAppGraph.getPathManager()
            )
        }
    }
}
