package dev.goood.chat_client.previews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.goood.chat_client.ui.chatScreen.MessageInput
import dev.goood.chat_client.viewModels.ChatViewModelPreview

@Preview(showSystemUi = true)
@Composable
fun MessageInputPreview() {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize()
    ) {
        MessageInput(
            chatID = 1,
            viewModel = ChatViewModelPreview(),
            modifier = Modifier
        )
    }
}