package dev.goood.chat_client

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.vinceglb.filekit.FileKit


fun main() = application {
    val state = rememberWindowState(
        width = 1300.dp,
        height = 1200.dp,
    )
    FileKit.init(appId = "PolyChat")
    Window(
        onCloseRequest = ::exitApplication,
        title = "PolyChat",
        state = state
    ) {
        App()
    }
}