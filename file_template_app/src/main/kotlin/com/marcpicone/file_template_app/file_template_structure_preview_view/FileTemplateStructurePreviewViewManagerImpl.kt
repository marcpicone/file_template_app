package com.marcpicone.file_template_app.file_template_structure_preview_view

import com.marcpicone.file_template_app.feature.FeatureManager
import com.marcpicone.file_template_app.file_template.FileTemplate
import com.marcpicone.file_template_app.file_template.FileTemplateManager
import com.marcpicone.file_template_app.file_template_feature_view.FileTemplateFeatureViewManager
import com.marcpicone.file_template_app.path.PathManager
import com.marcpicone.file_template_app.velocity_engine.VelocityEngineManager
import java.io.StringWriter
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.math.min
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine

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
        return collectAllOutputPaths().takeIf { it.isNotEmpty() }?.toFileTemplateStructure()
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

    private fun collectAllOutputPaths(): List<String> =
        featureManager.getFeatureIds().flatMap { featureId ->
            val feature = featureManager.getFeature(featureId)
                ?: throw IllegalArgumentException("Could not find feature for featureId : $featureId")
            feature.attachedTemplateIds.map { templateId ->
                fileTemplateManager.getFileTemplate(fileTemplateId = templateId)
            }.flatMap { fileTemplate ->
                processFileTemplate(fileTemplate = fileTemplate, featureName = feature.name)
            }
        }

    private fun processFileTemplate(
        fileTemplate: FileTemplate.ParentFileTemplate,
        featureName: String
    ): List<String> {
        val engine = velocityEngineManager.getEngine()
        val context = velocityEngineManager.getEngineContext(
            fileTemplate = fileTemplate,
            featureName = featureName.takeIf { it.isNotBlank() }
        )
        val basePath = pathManager.getPathToCurrentFolder()

        val parentName = evaluate(engine = engine, context = context, template = fileTemplate.filePath)
        val parentPath = Paths.get("$basePath/$parentName.${fileTemplate.fileExtension}")
        val results = mutableListOf(parentPath.toString())

        fileTemplate.child.forEach { childTemplate ->
            val childName = evaluate(engine = engine, context = context, template = childTemplate.filePath)
            results += "$basePath/$childName.${childTemplate.fileExtension}"
        }

        parentPath.parent
            ?.toFile()
            ?.takeIf { it.exists() }
            ?.listFiles()
            ?.mapTo(results) { it.path }

        return results
    }

    private fun evaluate(engine: VelocityEngine, context: VelocityContext, template: String): String {
        return StringWriter()
            .also { writer ->
                engine.evaluate(context, writer, "FilePath", template)
            }.toString().trim()
    }

    private fun List<String>.toFileTemplateStructure(): FileTemplateStructure {
        val absolutePaths = map { Paths.get(it).toAbsolutePath().normalize() }
        val root = deriveCommonRoot(absolutePaths)
        return buildStructureTree(root, absolutePaths)
    }

    private fun deriveCommonRoot(paths: List<Path>): Path {
        val first = paths.first()
        val srcIndex = (0 until first.nameCount).firstOrNull { first.getName(it).toString() == "src" }
        return if (srcIndex != null && srcIndex > 0) {
            first.root.resolve(first.subpath(0, srcIndex))
        } else {
            paths.reduce { a, b -> createCommonRoot(a, b) }
        }
    }

    private fun createCommonRoot(a: Path, b: Path): Path {
        val max = min(a.nameCount, b.nameCount)
        var i = 0
        while (i < max && a.getName(i) == b.getName(i)) i++
        return a.root.resolve(a.subpath(0, i))
    }

    private fun buildStructureTree(
        root: Path,
        paths: List<Path>
    ): FileTemplateStructure {
        val rootNode = DirNode(name = root.fileName?.toString() ?: root.toString(), path = root)

        paths.forEach { path ->
            val relativePath = try {
                root.relativize(path)
            } catch (_: IllegalArgumentException) {
                return@forEach
            }
            var cursor = rootNode
            for (index in 0 until relativePath.nameCount) {
                val fileName = relativePath.getName(index).toString()
                if (index == relativePath.nameCount - 1) {
                    cursor.files += FileTemplateStructure.FileTemplateStructureFile(
                        path = path.toString(),
                        name = fileName
                    )
                } else {
                    cursor = cursor.subDirs.getOrPut(fileName) {
                        DirNode(name = fileName, path = cursor.path.resolve(fileName))
                    }
                }
            }
        }

        return rootNode.toStructure()
    }

    private fun DirNode.toStructure(): FileTemplateStructure.FileTemplateStructureFolder {
        return FileTemplateStructure.FileTemplateStructureFolder(
            path = path.toString(),
            name = name,
            details = subDirs.values
                .sortedBy { it.name }
                .map { it.toStructure() } + files.sortedBy { it.name }
        )
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

    private data class DirNode(
        val name: String,
        val path: Path,
        val subDirs: MutableMap<String, DirNode> = mutableMapOf(),
        val files: MutableList<FileTemplateStructure.FileTemplateStructureFile> = mutableListOf()
    )
}
