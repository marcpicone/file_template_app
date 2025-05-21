package com.marcpicone.file_template_app.path

import java.io.File

interface PathManager {

    fun initialize()

    fun getInitialized(): Boolean

    fun setPathToCurrentFolder(path: String)

    fun getPath(fileName: String): String?

    /**
     * @return current application path before `/src`
     */
    fun getRootPath(): String?

    /**
     * null if no `res` or `resources` folder found
     */
    fun getAndroidRelativeResPath(): String?

    /**
     * null if no `res` or `resources` folder found
     */
    fun getAndroidAbsoluteResPath(): String?

    fun getAndroidPackageName(): String?

    fun getPathToCurrentFolder(): String?

    /**
     * Listen to [Listener.onChanged] to get file once loaded
     */
    fun getTemplateFolderFile(): File?

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    interface Listener {
        fun onChanged()
    }
}
