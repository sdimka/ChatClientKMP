package dev.goood.chat_client.ui.platformComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
actual fun PlatformDragAndDropArea(
    content: @Composable (() -> Unit),
    modifier: Modifier
) {

    val isDragging by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .background(if (isDragging) Color(0x2200AAFF) else Color.White)
            .padding(16.dp)
    ) {
        Text(
            "Drop files here:\n" //+
//                    files.joinToString("\n") { it.name }
        )
    }
}