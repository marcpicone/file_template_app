package com.marcpicone.file_template_app.path.internal

interface FileFinderManager {

    fun initialize()

    fun getInitialized(): Boolean

    fun getFilePath(fileName: String): String?

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {

        fun onChanged()
    }
}
