package com.marcpicone.file_template_app.velocity_engine

import com.marcpicone.file_template_app.file_template.FileTemplate
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine

/**
 * Velocity engine resource loader : https://velocity.apache.org/engine/devel/developer-guide.html#resource-loaders
 */
interface VelocityEngineManager {

    fun initialize()

    fun getInitialized(): Boolean

    /**
     * @param featureName null if no feature name filled by user
     * if not null, create folder with `featureName` and files in this folder with `featureName` to CamelCase as prefix
     * if null create files in current folder.
     * In this case the files will have the folder name to CamelCase as prefix
     * This method does not trigger the listener
     */
    fun getEngineContext(fileTemplate: FileTemplate.ParentFileTemplate, featureName: String?): VelocityContext

    fun getEngine(): VelocityEngine

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }
}
