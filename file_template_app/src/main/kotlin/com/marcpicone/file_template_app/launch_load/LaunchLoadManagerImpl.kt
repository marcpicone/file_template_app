package com.marcpicone.file_template_app.launch_load

import com.marcpicone.file_template_app.file_template.FileTemplateManager
import com.marcpicone.file_template_app.path.PathManager
import com.marcpicone.file_template_app.velocity_engine.VelocityEngineManager

class LaunchLoadManagerImpl(
    private val pathManager: PathManager,
    private val fileTemplateManager: FileTemplateManager,
    private val velocityEngineManager: VelocityEngineManager
) : LaunchLoadManager {

    private val listeners = ArrayList<LaunchLoadManager.Listener>()
    private var status: LaunchLoadManager.Status = LaunchLoadManager.Status.IDLE

    override fun initialize() {
        if (status != LaunchLoadManager.Status.IDLE) {
            return
        }
        status = LaunchLoadManager.Status.LOADING
        listeners.forEach { it.onChanged() }

        // Path manager must be initialized first, other managers depend on it
        pathManager.initialize()

        fileTemplateManager.initialize()
        velocityEngineManager.initialize()

        pathManager.addListener(createPathListener())
        fileTemplateManager.addListener(createFileTemplateParserListener())
        velocityEngineManager.addListener(createVelocityEngineListener())
    }

    override fun getStatus(): LaunchLoadManager.Status {
        return status
    }

    override fun addListener(listener: LaunchLoadManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: LaunchLoadManager.Listener) {
        listeners.remove(listener)
    }

    private fun createPathListener(): PathManager.Listener {
        return object : PathManager.Listener {
            override fun onChanged() {
                if (
                    pathManager.getInitialized() &&
                    fileTemplateManager.getInitialized() &&
                    velocityEngineManager.getInitialized()
                ) {
                    status = LaunchLoadManager.Status.LOADED
                    listeners.forEach { it.onChanged() }
                }
            }
        }
    }

    private fun createFileTemplateParserListener(): FileTemplateManager.Listener {
        return object : FileTemplateManager.Listener {
            override fun onChanged() {
                if (
                    pathManager.getInitialized() &&
                    fileTemplateManager.getInitialized() &&
                    velocityEngineManager.getInitialized()
                ) {
                    status = LaunchLoadManager.Status.LOADED
                    listeners.forEach { it.onChanged() }
                }
            }
        }
    }

    private fun createVelocityEngineListener(): VelocityEngineManager.Listener {
        return object : VelocityEngineManager.Listener {
            override fun onChanged() {
                if (
                    pathManager.getInitialized() &&
                    fileTemplateManager.getInitialized() &&
                    velocityEngineManager.getInitialized()
                ) {
                    status = LaunchLoadManager.Status.LOADED
                    listeners.forEach { it.onChanged() }
                }
            }
        }
    }
}
