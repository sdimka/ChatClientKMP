package dev.goood.chat_client.ui.platformComposable

import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable

@Composable
actual fun PlatformContextMenu (
    selectedTextProvider: () -> String,
    content: @Composable () -> Unit
) {
    ContextMenuDataProvider(items = {
        listOf(
            ContextMenuItem("User-defined action") {

            },
            ContextMenuItem("Another user-defined action") {
                // Another custom action
            }
        )
    }) {
        content()
    }
}