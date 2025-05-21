package com.marcpicone.file_template_app.application

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.marcpicone.file_template_app.graph.FileTemplateAppGraph
import com.marcpicone.file_template_app.main_view.MainView
import com.marcpicone.file_template_app.theme.FileTemplateTheme
import java.io.File

fun main(args: Array<String>) = application {
    val folderFromIde = args.getOrNull(0)
    val packageName = args.getOrNull(1)

    FileTemplateAppGraph.initialize(
        pathToCurrentFolder = createPathToCurrentFolder(folderFromIde),
        packageName = packageName
    )

    Window(
        title = "File Template",
        state = WindowState(placement = WindowPlacement.Maximized),
        onCloseRequest = ::exitApplication,
        resizable = true,
        undecorated = true,
        onKeyEvent = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Escape) {
                exitApplication()
            }
            false
        }
    ) {
        FileTemplateTheme {
            MainView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
    }
}

private fun createPathToCurrentFolder(folderFromIde: String?): String? {
    val folderForIde = folderFromIde ?: return null
    val file = File(folderForIde)
    return if (file.isDirectory) folderForIde else file.parent
}
