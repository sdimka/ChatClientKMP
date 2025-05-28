package dev.goood.chat_client.ui.platformComposable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformDragAndDropArea (
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
)