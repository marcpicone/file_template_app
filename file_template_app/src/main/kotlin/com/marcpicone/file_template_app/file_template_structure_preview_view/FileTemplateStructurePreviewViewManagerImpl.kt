package com.marcpicone.file_template_app.file_template_structure_preview_view

import com.marcpicone.file_template_app.feature.FeatureManager
import com.marcpicone.file_template_app.file_template.FileTemplateManager
import com.marcpicone.file_template_app.file_template_feature_view.FileTemplateFeatureViewManager
import com.marcpicone.file_template_app.path.PathManager
import com.marcpicone.file_template_app.velocity_engine.VelocityEngineManager
import java.io.File
import java.io.StringWriter
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.math.min

class FileTemplateStructurePreviewViewManagerImpl(
    private val featureManager: FeatureManager,
    fileTemplateFeatureViewManager: FileTemplateFeatureViewManager,
    private val fileTemplateManager: FileTemplateManager,
    private val pathManager: PathManager,
    private val velocityEngineManager: VelocityEngineManager
) : FileTemplateStructurePreviewViewManager {

    private val listeners = ArrayList<FileTemplateStructurePreviewViewManager.Listener>()

    init {
        featureManager.addListener(createFeatureListener())
        fileTemplateFeatureViewManager.addListener(createFileTemplateFeatureViewListener())
    }

    override fun getStructure(): FileTemplateStructure? {
        val outputPaths = ArrayList<String>()
        val featureIds = featureManager.getFeatureIds()
        val velocityEngine = velocityEngineManager.getEngine()
        featureIds.forEach { featureId ->
            val currentFeature = featureManager.getFeature(featureId)
                ?: throw IllegalStateException("No current feature for featureId $featureId")
            val fileTemplates = currentFeature.attachedTemplateIds.map { fileTemplateManager.getFileTemplate(it) }
            fileTemplates.forEach { fileTemplate ->
                val fileExtension = fileTemplate.fileExtension
                val velocityContext = velocityEngineManager.getEngineContext(
                    fileTemplate = fileTemplate,
                    featureName = currentFeature.name.takeIf { it.trim().isNotEmpty() }
                )

                val fileNameWriter = StringWriter()
                val child = fileTemplate.child
                val filePath = fileTemplate.filePath
                val pathToCurrentFolder = pathManager.getPathToCurrentFolder()

                velocityEngine.evaluate(
                    /* context = */ velocityContext,
                    /* out = */ fileNameWriter,
                    /* logTag = */ "filePath",
                    /* instring = */ filePath
                )

                val parentFolderName = fileNameWriter.toString().trim()
                val parentOutputPath = Paths.get("$pathToCurrentFolder/$parentFolderName.$fileExtension")
                val parentPathname = parentOutputPath.toString()
                outputPaths.add(parentPathname)

                for (i in child.indices) {
                    val childTemplate = child[i]
                    val childFileNameWriter = StringWriter()
                    val childFileExtension = childTemplate.fileExtension

                    velocityEngine.evaluate(
                        velocityContext,
                        childFileNameWriter,
                        "filePath",
                        childTemplate.filePath
                    )

                    val childFileName = childFileNameWriter.toString().trim()
                    val childOutputPath = Paths.get("$pathToCurrentFolder/$childFileName.$childFileExtension")
                    outputPaths.add(childOutputPath.toString())
                }
                // Add other files in parent folder
                val removeLast = parentPathname.substringBeforeLast("/")
                val parentFile = File(removeLast)
                val otherFilePaths = if (parentFile.exists()) {
                    parentFile.listFiles().map { it.path }
                } else {
                    emptyList()
                }
                outputPaths.addAll(otherFilePaths)
            }
        }
        if (outputPaths.isEmpty()) {
            return null
        }
        return outputPaths.toFileTemplateStructure()
    }

    override fun addListener(listener: FileTemplateStructurePreviewViewManager.Listener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    override fun removeListener(listener: FileTemplateStructurePreviewViewManager.Listener) {
        listeners.remove(listener)
    }

    private fun ArrayList<String>.toFileTemplateStructure(): FileTemplateStructure {
        val absolutePaths: List<Path> = this.map {
            Paths.get(it)
                .toAbsolutePath()
                .normalize()
        }
        val first = absolutePaths.firstOrNull()
            ?: error("No absolute path found")

        val srcIndex = (0 until first.nameCount)
            .firstOrNull { first.getName(it).toString() == "src" }

        val rootPath: Path = if (srcIndex != null && srcIndex > 0) {
            first.root.resolve(first.subpath(0, srcIndex))
        } else {
            absolutePaths.reduce { actualPath, nextPath ->
                createCommonRoot(
                    absolutePathA = actualPath,
                    absolutePathB = nextPath
                )
            }
        }

        val rootNode = DirNode(name = rootPath.fileName?.toString() ?: rootPath.toString(), path = rootPath)

        for (path in absolutePaths) {
            val relativePath = try {
                rootPath.relativize(path)
            } catch (_: IllegalArgumentException) {
                // Out of root -> skip
                continue
            }
            var currentRootNode = rootNode
            for (i in 0 until relativePath.nameCount) {
                val part = relativePath.getName(i).toString()
                if (i == relativePath.nameCount - 1) {
                    // Last part always a file in FileTemplateStructure
                    currentRootNode.files += FileTemplateStructure.FileTemplateStructureFile(
                        path = path.toString(),
                        name = part
                    )
                } else {
                    currentRootNode = currentRootNode
                        .subDirs
                        .getOrPut(part) {
                            DirNode(name = part, path = currentRootNode.path.resolve(part))
                        }
                }
            }
        }
        return rootNode.toStructure()
    }

    private fun createCommonRoot(absolutePathA: Path, absolutePathB: Path): Path {
        val maxSegments = min(absolutePathA.nameCount, absolutePathB.nameCount)
        var i = 0
        while (i < maxSegments && absolutePathA.getName(i) == absolutePathB.getName(i)) {
            i++
        }
        return absolutePathA.root.resolve(absolutePathA.subpath(0, i))
    }

    private fun DirNode.toStructure(): FileTemplateStructure =
        FileTemplateStructure.FileTemplateStructureFolder(
            path = path.toString(),
            name = name,
            details = subDirs.values
                .sortedBy { it.name }
                .map { it.toStructure() } +
                files.sortedBy { it.name }
        )

    private data class DirNode(val name: String, val path: Path) {
        val subDirs = mutableMapOf<String, DirNode>()
        val files = mutableListOf<FileTemplateStructure.FileTemplateStructureFile>()
    }

    private fun createFeatureListener() = object : FeatureManager.Listener {
        override fun onChanged() {
            listeners.forEach { it.onChanged() }
        }
    }

    private fun createFileTemplateFeatureViewListener() = object : FileTemplateFeatureViewManager.Listener {
        override fun onChanged() {
            listeners.forEach { it.onChanged() }
        }
    }
}
