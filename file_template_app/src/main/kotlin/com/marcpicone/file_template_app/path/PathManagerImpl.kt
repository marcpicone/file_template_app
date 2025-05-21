package com.marcpicone.file_template_app.path

import androidx.annotation.WorkerThread
import com.marcpicone.file_template_app.path.internal.FileFinderManager
import java.io.File
import java.io.FileOutputStream
import java.net.JarURLConnection
import kotlin.io.path.createTempDirectory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing

class PathManagerImpl(
    private val fileFinderManager: Lazy<FileFinderManager>, // break circular dependency
    pathToCurrentFolder: String?,
    private val packageName: String?
) : PathManager {

    private var initializeCall = false
    private var initialized = false
    private var templateFolderFile: File? = null
    private var innerPathToCurrentFolder: String? = pathToCurrentFolder
    private var listeners = ArrayList<PathManager.Listener>()

    override fun initialize() {
        if (initializeCall) {
            return
        }
        initializeCall = true
        CoroutineScope(Dispatchers.IO).launch {
            val resourceUrl = this::class.java.getResource("/file_templates")
                ?: throw IllegalStateException("No template folder found")
            val templateFolderFile = when (resourceUrl.protocol) {
                "file" -> File(resourceUrl.toURI())
                "jar" -> copyJarResources()
                else -> throw IllegalStateException("Unsupported protocol: ${resourceUrl.protocol}")
            }
            CoroutineScope(Dispatchers.Swing).launch {
                this@PathManagerImpl.templateFolderFile = templateFolderFile
                fileFinderManager.value.initialize()
                fileFinderManager.value.addListener(createFileFinderListener())
                val fileFinderInitialized = fileFinderManager.value.getInitialized()
                if (fileFinderInitialized) {
                    initialized = true
                    listeners.forEach { it.onChanged() }
                }
            }
        }
    }

    override fun getInitialized(): Boolean {
        return initialized
    }

    override fun setPathToCurrentFolder(path: String) {
        if (innerPathToCurrentFolder == path) {
            return
        }
        innerPathToCurrentFolder = path
        listeners.forEach { it.onChanged() }
    }

    override fun getPath(fileName: String): String? {
        return fileFinderManager.value.getFilePath(fileName)
    }

    override fun getRootPath(): String? {
        return innerPathToCurrentFolder?.substringBefore("/src/")
    }

    override fun getAndroidRelativeResPath(): String? {
        val pathToCurrentFolder = innerPathToCurrentFolder ?: return null
        val mainIndex = pathToCurrentFolder.indexOf("/src/main/")
        if (mainIndex == -1) return null

        val baseMainFolder = pathToCurrentFolder.substring(0, mainIndex) + "/src/main"
        val appMainFolder = File(baseMainFolder)

        val resourceFolder = appMainFolder.listFiles()?.firstOrNull { file ->
            file.isDirectory && (file.name == "res" || file.name == "resources")
        } ?: return null

        val currentPath = File(pathToCurrentFolder).toPath()
        val resourcePath = resourceFolder.toPath()
        val relativePath = currentPath.relativize(resourcePath)

        return relativePath.toString()
    }

    override fun getAndroidAbsoluteResPath(): String? {
        val innerPathToCurrentFolder = innerPathToCurrentFolder ?: return null
        val mainIndex = innerPathToCurrentFolder.indexOf("/src/main/")
        if (mainIndex == -1) return null

        val baseMainFolder = innerPathToCurrentFolder.substring(0, mainIndex) + "/src/main"
        val appMainFolder = File(baseMainFolder)

        val resourceFolder = appMainFolder.listFiles()?.firstOrNull { file ->
            file.isDirectory && (file.name == "res" || file.name == "resources")
        } ?: return null

        return resourceFolder.toPath().toString()
    }

    override fun getAndroidPackageName(): String? {
        val substringAfterMain = innerPathToCurrentFolder?.substringAfter("main/") ?: return null
        val javaOrKotlinFolder = substringAfterMain.split("/").first().plus("/")
        val packageName = substringAfterMain.removePrefix(javaOrKotlinFolder).replace("/", ".")

        return this.packageName ?: packageName
    }

    override fun getPathToCurrentFolder(): String? {
        return innerPathToCurrentFolder
    }

    override fun getTemplateFolderFile(): File? {
        if (!initializeCall) {
            throw IllegalStateException("Please call initialize first")
        }
        return templateFolderFile
    }

    override fun addListener(listener: PathManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: PathManager.Listener) {
        listeners.remove(listener)
    }

    @WorkerThread
    private fun copyJarResources(): File {
        return runBlocking {
            val resourcePath = "file_templates"
            val targetDir = createTempDirectory(prefix = resourcePath).toFile()
            val resourceUrl = this::class.java.getResource("/$resourcePath")
                ?: throw IllegalStateException("Resource $resourcePath not found")
            val jarConnection = resourceUrl.openConnection() as JarURLConnection
            val jarFile = jarConnection.jarFile
            jarFile.entries().asSequence().forEach { entry ->
                if (entry.name.startsWith("$resourcePath/") && !entry.isDirectory) {
                    val relativePath = entry.name.removePrefix("$resourcePath/")
                    val targetFile = File(targetDir, relativePath)
                    targetFile.parentFile.mkdirs()
                    this::class.java.getResourceAsStream("/" + entry.name)?.use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
            targetDir
        }
    }

    private fun createFileFinderListener(): FileFinderManager.Listener {
        return object : FileFinderManager.Listener {
            override fun onChanged() {
                if (fileFinderManager.value.getInitialized()) {
                    initialized = true
                    listeners.forEach { it.onChanged() }
                }
            }
        }
    }

    companion object {

        fun String.snakeCaseToCamelCaseWithFirstCharLowerCase(): String {
            return this.split("_").joinToString("") { stringPart ->
                stringPart.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase() else char.toString()
                }
            }.replaceFirstChar { char ->
                if (char.isUpperCase()) char.lowercase() else char.toString()
            }
        }

        fun String.snakeCaseToCamelCaseWithFirstCharUpperCase(): String {
            return this.split("_").joinToString("") { stringPart ->
                stringPart.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase() else char.toString()
                }
            }
        }

        /**
         * From GPT:
         *  1.  ([a-z0-9])([A-Z]) → Detects a lowercase letter or digit followed by an uppercase letter (e.g., "camelCase" → "camel_Case").
         *  2.  ([A-Z])([A-Z][a-z]) → Handles an all-uppercase acronym followed by an uppercase then a lowercase letter (e.g., "JSONFormat" becomes "json_format" instead of "j_s_o_n_format").
         *  3.  Replacement: "$1$3_$2$4" → Inserts _ between the detected parts.
         */

        fun String.camelCaseToLowerSnakeCase(): String {
            return this.replace(Regex("([a-z0-9])([A-Z])|([A-Z])([A-Z][a-z])"), "$1$3_$2$4")
                .lowercase()
        }

        /**
         * From GPT:
         *  1.  ([a-z0-9])([A-Z]) → Detects a lowercase letter or digit followed by an uppercase letter (e.g., "camelCase" → "camel_Case").
         *  2.  ([A-Z])([A-Z][a-z]) → Handles an all-uppercase acronym followed by an uppercase then a lowercase letter (e.g., "JSONFormat" becomes "json_format" instead of "j_s_o_n_format").
         *  3.  Replacement: "$1$3_$2$4" → Inserts _ between the detected parts.
         */
        fun String.camelCaseToUpperSnakeCase(): String {
            return this.replace(Regex("([a-z0-9])([A-Z])|([A-Z])([A-Z][a-z])"), "$1$3_$2$4")
                .uppercase()
        }
    }
}
