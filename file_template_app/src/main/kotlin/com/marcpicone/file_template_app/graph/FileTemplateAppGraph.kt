package com.marcpicone.file_template_app.graph

import com.marcpicone.file_template_app.feature.FeatureModule
import com.marcpicone.file_template_app.file_explorer.FileExplorerModule
import com.marcpicone.file_template_app.file_template.FileTemplateModule
import com.marcpicone.file_template_app.file_template_creator.FileTemplateCreatorModule
import com.marcpicone.file_template_app.file_template_feature_view.FileTemplateFeatureViewModule
import com.marcpicone.file_template_app.file_template_structure_preview_view.FileTemplateStructurePreviewViewModule
import com.marcpicone.file_template_app.file_template_template_view.FileTemplateTemplateViewModule
import com.marcpicone.file_template_app.launch_load.LaunchLoadModule
import com.marcpicone.file_template_app.path.PathModule
import com.marcpicone.file_template_app.velocity_engine.VelocityEngineModule

class FileTemplateAppGraph(
    val pathToCurrentFolder: String?,
    val packageName: String?
) {

    private val featureManager by lazy { FeatureModule().createFeatureManager() }
    private val fileExplorerManager by lazy { FileExplorerModule().createFileExplorerManager() }
    private val fileTemplateCreatorManager by lazy { FileTemplateCreatorModule().createFileTemplateCreatorManager() }
    private val fileTemplateFeatureViewManager by lazy { FileTemplateFeatureViewModule().createFileTemplateFeatureViewManager() }
    private val fileTemplateManager by lazy { FileTemplateModule().createFileTemplateManager() }
    private val fileTemplateStructurePreviewViewManager by lazy { FileTemplateStructurePreviewViewModule().createFileTemplateStructurePreviewViewManager() }
    private val fileTemplateTemplateViewManager by lazy { FileTemplateTemplateViewModule().createFileTemplateTemplateViewManager() }
    private val launchLoadManager by lazy { LaunchLoadModule().createLaunchLoadManager() }
    private val pathManager by lazy { PathModule().createPathManager() }
    private val velocityEngineManager by lazy { VelocityEngineModule().createVelocityEngineManager() }

    companion object Companion {

        private var graph: FileTemplateAppGraph? = null

        fun initialize(pathToCurrentFolder: String?, packageName: String?) {
            if (graph != null) {
                return
            }
            graph = FileTemplateAppGraph(
                pathToCurrentFolder = pathToCurrentFolder,
                packageName = packageName
            )
            getLaunchLoadManager().initialize()
        }

        fun getFeatureManager() = graph!!.featureManager
        fun getFileExplorerManager() = graph!!.fileExplorerManager
        fun getFileTemplateCreatorManager() = graph!!.fileTemplateCreatorManager
        fun getFileTemplateFeatureViewManager() = graph!!.fileTemplateFeatureViewManager
        fun getFileTemplateManager() = graph!!.fileTemplateManager
        fun getFileTemplateStructurePreviewViewManager() = graph!!.fileTemplateStructurePreviewViewManager
        fun getFileTemplateTemplateViewManager() = graph!!.fileTemplateTemplateViewManager
        fun getLaunchLoadManager() = graph!!.launchLoadManager
        fun getPackageName() = graph!!.packageName
        fun getPathManager() = graph!!.pathManager
        fun getPathToCurrentFolder() = graph!!.pathToCurrentFolder
        fun getVelocityEngineManager() = graph!!.velocityEngineManager
    }
}
