package dev.goood.chat_client.ui.platformComposable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformDragAndDropArea (
    onFilesDropped: (List<String>) -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
)