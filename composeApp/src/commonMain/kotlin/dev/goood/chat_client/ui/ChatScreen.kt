package dev.goood.chat_client.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.goood.chat_client.viewModels.ChatViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ChatScreen(chatID: Int?) {

    val viewModel: ChatViewModel = koinViewModel()

    Text(
        text = "Chat screen, chat : $chatID"
    )
}