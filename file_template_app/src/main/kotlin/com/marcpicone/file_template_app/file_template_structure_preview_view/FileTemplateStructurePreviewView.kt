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

    val items = remember(rootFolder) { createFlattenStructureWithDescendants(rootFolder) }

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
        items(items) { info ->
            StructureRow(info)
        }
    }
}

@Composable
private fun StructureRow(info: NodeInfo) {
    val indent = 16.dp
    val color = info.depth.depthToColor()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .drawBehind {
                val px = indent.toPx()
                val yTop = 0f
                val yBottom = size.height
                val yMid = size.height / 2
                val strokeWidth = 1.dp.toPx()

                info.ancestorHasChild.forEachIndexed { level, hasChild ->
                    val isFile = info.node is FileTemplateStructure.FileTemplateStructureFile
                    val x = if (isFile) {
                        level * px
                    } else {
                        level * px + px
                    }

                    if (hasChild && level == info.ancestorHasChild.size - 1) {
                        val xStart = level * px
                        val xEnd = level * px + px
                        // horizontal
                        drawLine(
                            color = color,
                            strokeWidth = strokeWidth,
                            start = Offset(xStart, yMid),
                            end = Offset(xEnd, yMid)
                        )
                        // vertical to link to parent folder
                        drawLine(
                            color = color,
                            strokeWidth = strokeWidth,
                            start = Offset(x, yMid),
                            end = Offset(x, yBottom)
                        )
                    } else if (hasChild && level == info.ancestorHasChild.size - 2) {
                        val childX = level * px + px
                        // vertical to link to parent folder
                        drawLine(
                            color = color,
                            strokeWidth = strokeWidth,
                            start = Offset(childX, yTop),
                            end = Offset(childX, yMid)
                        )
                    }
                }

                val xStart = info.depth * px
                val xEnd = info.depth * px + px
                // horizontal
                drawLine(
                    color = color,
                    strokeWidth = strokeWidth,
                    start = Offset(xStart, yMid),
                    end = Offset(xEnd, yMid)
                )
            }
    ) {
        Spacer(Modifier.width(indent * (info.depth + 1)))
        if (info.node is FileTemplateStructure.FileTemplateStructureFolder) {
            FolderIconView(tint = color)
        } else {
            FileIconView(tint = info.color)
        }
        Text(
            text = info.node.name,
            color = info.color,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun FolderIconView(
    tint: Color
) {
    Icon(
        painter = painterResource(FOLDER_ICON_PATH),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.padding(end = 8.dp)
    )
}

@Composable
private fun FileIconView(
    tint: Color
) {
    Icon(
        painter = painterResource(FILE_ICON_PATH),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.padding(end = 8.dp)
    )
}

private fun createFlattenStructureWithDescendants(
    root: FileTemplateStructure.FileTemplateStructureFolder?
): List<NodeInfo> {
    if (root == null) {
        return emptyList()
    }
    val nodeInfos = createNodeRecursively(listOf(root), 0, emptyList())
    return nodeInfos
        .toMutableList()
        .apply {
            for (i in nodeInfos.indices) {
                val similarNodes = nodeInfos.filter { it.node == nodeInfos[i].node }
                if (similarNodes.size > 1) {
                    val lightGrayNode = similarNodes.find { it.color == Color.LightGray }
                    remove(lightGrayNode)
                }
            }
        }.distinct()
}

private fun createNodeRecursively(
    nodes: List<FileTemplateStructure>,
    depth: Int,
    ancestorFlags: List<Boolean>
): List<NodeInfo> {
    val nodeSet = mutableSetOf<FileTemplateStructure>()
    return nodes.flatMap { node ->
        val color = if (File(node.path).exists()) {
            val color = if (nodeSet.contains(node)) {
                Color.Red
            } else {
                Color.LightGray
            }
            nodeSet.add(node)
            color
        } else {
            Color.Green
        }
        val currentNodeInfo = NodeInfo(
            node = node,
            depth = depth,
            ancestorHasChild = ancestorFlags,
            color = color
        )
        val children = (node as? FileTemplateStructure.FileTemplateStructureFolder)?.details.orEmpty()
        if (children.isEmpty()) {
            listOf(currentNodeInfo)
        } else {
            listOf(currentNodeInfo) + createNodeRecursively(children, depth + 1, ancestorFlags + true)
        }
    }
}

private fun Int.depthToColor(): Color {
    return when (this) {
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
}

private data class NodeInfo(
    val node: FileTemplateStructure,
    val depth: Int,
    val ancestorHasChild: List<Boolean>,
    val color: Color
)

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
