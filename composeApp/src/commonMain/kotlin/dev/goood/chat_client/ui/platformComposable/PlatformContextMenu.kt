package dev.goood.chat_client.ui.platformComposable

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformContextMenu (
    selectedTextProvider: () -> String,
    content: @Composable () -> Unit
)