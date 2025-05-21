package com.marcpicone.file_template_app.velocity_engine

import com.marcpicone.file_template_app.file_template.FileTemplate
import com.marcpicone.file_template_app.path.PathManager
import com.marcpicone.file_template_app.path.PathManagerImpl.Companion.camelCaseToLowerSnakeCase
import com.marcpicone.file_template_app.path.PathManagerImpl.Companion.camelCaseToUpperSnakeCase
import com.marcpicone.file_template_app.path.PathManagerImpl.Companion.snakeCaseToCamelCaseWithFirstCharLowerCase
import com.marcpicone.file_template_app.path.PathManagerImpl.Companion.snakeCaseToCamelCaseWithFirstCharUpperCase
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine

class VelocityEngineManagerImpl(
    private val pathManager: PathManager
) : VelocityEngineManager {

    private val listeners = ArrayList<VelocityEngineManager.Listener>()
    private var engine = VelocityEngine()
    private var initializeCall = false
    private var initialized = false

    override fun initialize() {
        if (initializeCall) {
            return
        }
        initializeCall = true
        engine.apply {
            setProperty("resource.loader", "classpath")
            setProperty("classpath.resource.loader.path", "file_templates")
            setProperty(
                "classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"
            )
            setProperty("velocimacro.context.localscope", false)
        }
        engine.init()
        initialized = true
        listeners.forEach { it.onChanged() }
    }

    override fun getInitialized(): Boolean {
        return initialized
    }

    override fun getEngineContext(
        fileTemplate: FileTemplate.ParentFileTemplate,
        featureName: String?
    ): VelocityContext {
        return VelocityContext().apply {
            createElements(featureName = featureName).forEach { (key, value) -> put(key, value) }
        }
    }

    override fun getEngine(): VelocityEngine {
        return engine
    }

    override fun addListener(listener: VelocityEngineManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: VelocityEngineManager.Listener) {
        listeners.remove(listener)
    }

    private fun createElements(
        featureName: String?,
    ): Map<String, String?> {
        // @formatter:off
        val currentFeatureName = featureName.takeIf { !it?.trim().isNullOrEmpty() } ?: pathManager.getPathToCurrentFolder()!!.split("/").last()
        // @formatter:on

        return mapOf(
            // @formatter:off
            "FEATURE_NAME" to currentFeatureName,
            "FEATURE_PATH" to featureName,
            "RES_PATH" to if (pathManager.getAndroidRelativeResPath().isNullOrEmpty()) "" else "${pathManager.getAndroidRelativeResPath()}/",
            "ANDROID_PACKAGE" to if (featureName.isNullOrEmpty()) "package ${pathManager.getAndroidPackageName()}" else "package ${pathManager.getAndroidPackageName()}.$featureName",
            "ANDROID_PACKAGE_NAME" to pathManager.getAndroidPackageName(),
            "FEATURE_NAME_TO_UPPER_CAMEL_CASE" to currentFeatureName.snakeCaseToCamelCaseWithFirstCharUpperCase(),
            "FEATURE_NAME_TO_LOWER_CAMEL_CASE" to currentFeatureName.snakeCaseToCamelCaseWithFirstCharLowerCase(),
            "FEATURE_NAME_TO_LOWER_SNAKE_CASE" to currentFeatureName.camelCaseToLowerSnakeCase(),
            "FEATURE_NAME_TO_UPPER_SNAKE_CASE" to currentFeatureName.camelCaseToUpperSnakeCase()
            // @formatter:on
        )
    }
}
