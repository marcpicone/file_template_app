package com.marcpicone.file_template_app.file_template_structure_preview_view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.marcpicone.file_template_app.file_template_structure_preview_view.FileTemplateStructurePreviewViewContract.Companion.FILE_ICON_PATH
import com.marcpicone.file_template_app.file_template_structure_preview_view.FileTemplateStructurePreviewViewContract.Companion.FOLDER_ICON_PATH
import com.marcpicone.file_template_app.graph.FileTemplateAppGraph
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FileTemplateStructurePreviewView(
    modifier: Modifier = Modifier
) {
    val presenter = createPresenter()
    val uiState = presenter.uiState
    val event = presenter.event

    LaunchedEffect(Unit) { event.onAttached() }
    DisposableEffect(Unit) { onDispose { event.onDetached() } }

    Content(uiState = uiState, event = event, modifier = modifier)
}

@Composable
private fun Content(
    uiState: FileTemplateStructurePreviewViewContract.UiState,
    event: FileTemplateStructurePreviewViewContract.Event,
    modifier: Modifier = Modifier
) {
    val structure = uiState.structure.collectAsState()
    val rootFolder = structure.value as? FileTemplateStructure.FileTemplateStructureFolder

    FileTreeView(
        rootFolder = rootFolder,
        modifier = modifier
    )
}

@Composable
private fun FileTreeView(
    rootFolder: FileTemplateStructure.FileTemplateStructureFolder?,
    modifier: Modifier = Modifier
) {
    val flatList = remember(rootFolder) { flattenStructure(rootFolder) }
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(size = 20.dp)
            ),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(flatList) { nodeInfo ->
            FileNodeRow(nodeInfo)
        }
    }
}

@Composable
private fun FileNodeRow(info: NodeInfo) {
    val indentStep: Dp = 16.dp
    val lineColor = info.depth.lineColor()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .drawHierarchyLines(info = info, indent = indentStep, color = lineColor)
    ) {
        Spacer(Modifier.width(indentStep * (info.depth + 1)))
        FileOrFolderIcon(isFolder = info.isFolder, tint = info.color)
        Text(
            text = info.node.name,
            color = info.color,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

private fun Modifier.drawHierarchyLines(
    info: NodeInfo,
    indent: Dp,
    color: Color
): Modifier = this.drawBehind {
    val px = indent.toPx()
    val midY = size.height / 2
    val bottomY = size.height
    val stroke = 1.dp.toPx()

    info.ancestorHasChild.forEachIndexed { level, hasChild ->
        if (!hasChild) return@forEachIndexed
        val x = level * px + if (info.isFolder) px else 0f
        if (level == info.ancestorHasChild.lastIndex) {
            // horizontal
            drawLine(color, strokeWidth = stroke, start = Offset(level * px, midY), end = Offset(level * px + px, midY))
            // vertical down
            drawLine(color, strokeWidth = stroke, start = Offset(x, midY), end = Offset(x, bottomY))
        } else if (level == info.ancestorHasChild.lastIndex - 1) {
            // vertical up
            drawLine(
                color,
                strokeWidth = stroke,
                start = Offset(level * px + px, 0f),
                end = Offset(level * px + px, midY)
            )
        }
    }

    val startX = info.depth * px
    drawLine(color, strokeWidth = stroke, start = Offset(startX, midY), end = Offset(startX + px, midY))
}

@Composable
private fun FileOrFolderIcon(isFolder: Boolean, tint: Color) {
    val iconPath = if (isFolder) FOLDER_ICON_PATH else FILE_ICON_PATH
    Icon(
        painter = painterResource(iconPath),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.padding(end = 8.dp)
    )
}

private fun flattenStructure(root: FileTemplateStructure?): List<NodeInfo> {
    if (root == null) return emptyList()
    val collected = mutableListOf<NodeInfo>()
    collectNodes(node = root, depth = 0, ancestors = emptyList(), collected = collected)
    // remove duplicate light-gray entries if same node appears multiple times
    return collected.distinctBy { it.node.path to it.color }
}

private fun collectNodes(
    node: FileTemplateStructure,
    depth: Int,
    ancestors: List<Boolean>,
    collected: MutableList<NodeInfo>
) {
    val exists = File(node.path).exists()
    val color = when {
        !exists -> Color.Green
        collected.map { it.node }.contains(node) -> Color.Red
        else -> Color.LightGray
    }
    collected += NodeInfo(node = node, depth = depth, ancestorHasChild = ancestors, color = color)
    val children = (node as? FileTemplateStructure.FileTemplateStructureFolder)?.details.orEmpty()
    if (children.isNotEmpty()) {
        children.forEach { child ->
            collectNodes(child, depth + 1, ancestors + true, collected)
        }
    }
}

private fun Int.lineColor(): Color = when (this) {
    0 -> Color.DarkGray
    1 -> Color.Gray
    2 -> Color.LightGray
    3 -> Color.Cyan
    4 -> Color.Blue
    5 -> Color.Magenta
    6 -> Color.Red
    7 -> Color.Yellow
    8 -> Color.Green
    else -> Color.White
}

private data class NodeInfo(
    val node: FileTemplateStructure,
    val depth: Int,
    val ancestorHasChild: List<Boolean>,
    val color: Color
) {
    val isFolder: Boolean
        get() = node is FileTemplateStructure.FileTemplateStructureFolder
}

@Composable
private fun createPresenter(): FileTemplateStructurePreviewViewContract {
    if (LocalInspectionMode.current) {
        return object : FileTemplateStructurePreviewViewContract {
            override val event: FileTemplateStructurePreviewViewContract.Event
                get() = object : FileTemplateStructurePreviewViewContract.Event {
                    override fun onAttached() {}
                    override fun onDetached() {}
                }
            override val uiState: FileTemplateStructurePreviewViewContract.UiState
                get() = object : FileTemplateStructurePreviewViewContract.UiState {
                    override val structure: StateFlow<FileTemplateStructure?>
                        get() = MutableStateFlow(
                            FileTemplateStructure.FileTemplateStructureFolder(
                                path = "Folder",
                                name = "Folder",
                                details = listOf(
                                    FileTemplateStructure.FileTemplateStructureFile(
                                        path = "File",
                                        name = "File",
                                    )
                                )
                            )
                        )
                }
        }
    }
    return FileTemplateStructurePreviewViewPresenter(
        lazy { FileTemplateAppGraph.getFileTemplateStructurePreviewViewManager() },
        lazy { FileTemplateAppGraph.getFileTemplateTemplateViewManager() }
    )
}
