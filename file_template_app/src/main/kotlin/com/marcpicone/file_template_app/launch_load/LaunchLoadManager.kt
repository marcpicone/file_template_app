package com.marcpicone.file_template_app.launch_load

interface LaunchLoadManager {

    fun initialize()

    fun getStatus(): Status

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }

    enum class Status {
        IDLE,
        LOADING,
        LOADED
    }
}
