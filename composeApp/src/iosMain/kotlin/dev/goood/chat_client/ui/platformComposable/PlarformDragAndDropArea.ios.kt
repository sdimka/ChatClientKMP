package dev.goood.chat_client.ui.platformComposable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformDragAndDropArea(
    onFilesDropped: (List<String>) -> Unit,
    content: @Composable (() -> Unit),
    modifier: Modifier
) {

}