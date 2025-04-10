package dev.goood.chat_client

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState


fun main() = application {
    val state = rememberWindowState(
        width = 800.dp,
        height = 1200.dp,
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinProject",
        state = state
    ) {
        App()
    }
}