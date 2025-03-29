package dev.goood.chat_client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.goood.chat_client.di.initKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinProject",
    ) {
        initKoin()
        App()
    }
}