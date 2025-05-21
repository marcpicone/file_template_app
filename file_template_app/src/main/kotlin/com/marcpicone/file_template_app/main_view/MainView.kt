package com.marcpicone.file_template_app.main_view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marcpicone.file_template_app.file_template_view.FileTemplateView
import com.marcpicone.file_template_app.graph.FileTemplateAppGraph
import kotlin.system.exitProcess

@Composable
fun MainView(
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
    uiState: MainViewContract.UiState,
    event: MainViewContract.Event,
    modifier: Modifier = Modifier
) {
    val loading = uiState.loading.collectAsState().value
    val pathToCurrentFolder = uiState.pathToCurrentFolder.collectAsState().value

    if (pathToCurrentFolder == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = { event.onSelectDirectoryClicked() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Select a directory to inject template"
                )
            }
        }
    } else {
        Column(
            modifier = modifier
        ) {
            TopBar(
                menuItems = uiState.menuItems,
                selected = {
                    // for the moment only one menu item
                    true
                },
                onMenuItemClick = {
                    // for the moment only one menu item
                    // do nothing
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.weight(1f).align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                FileTemplateView(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TopBar(
    menuItems: List<MainViewContract.MenuItem>,
    selected: (menuItem: MainViewContract.MenuItem) -> Boolean,
    onMenuItemClick: (MainViewContract.MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
    ) {
        item {
            IconButton(
                onClick = { exitProcess(0) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        items(items = menuItems) { menuItem ->
            Button(
                onClick = { onMenuItemClick(menuItem) }
            ) {
                Text(
                    text = menuItem.toText(),
                    color = if (selected(menuItem)) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    }
                )
            }
        }
    }
}

private fun MainViewContract.MenuItem.toText(): String {
    return when (this) {
        MainViewContract.MenuItem.TEMPLATE -> {
            "template"
        }
    }
}

private fun createPresenter(): MainViewContract {
    return MainViewPresenter(
        FileTemplateAppGraph.getLaunchLoadManager(),
        lazy { FileTemplateAppGraph.getFileExplorerManager() },
        lazy { FileTemplateAppGraph.getPathManager() }
    )
}
