package dev.goood.chat_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.viewModels.ChatViewModel
import io.ktor.util.reflect.instanceOf
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ChatScreen(
    chatID: Int?,
    modifier: Modifier = Modifier
) {

    val viewModel: ChatViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (chatID != null) {
            viewModel.getMessages(chatID)
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(bottom = 50.dp),
    ) {
        MessageList(
            viewModel = viewModel,
            modifier = modifier.padding(bottom = 20.dp)
        )

        MessageInput(
            viewModel = viewModel,
        )
    }
}

@Composable
fun MessageList(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
//    val messageState = state ?: return

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ChatViewModel.State.Loading -> {
                CircularProgressIndicator()
            }

            is ChatViewModel.State.Success -> {
//                val messageItems = messageState.messageListItem.items // 4
//                    .filterIsInstance<MessageListItem.MessageItem>()
//                    .filter { it.message.text.isNotBlank() }
//                    .asReversed()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true, // 5
                ) {
                    items(viewModel.messages.value) { message ->
                        MessageElement(message) // 6
                    }
                }
            }

            is ChatViewModel.State.Error -> {
                Text((state as ChatViewModel.State.Error).message)
            }
        }
    }
}


@Composable
fun MessageElement(
    message: Message,
    onDeleteClick: (message: Message) -> Unit = {},
) {
    Column (
        modifier = Modifier.padding(10.dp).background(Color.Cyan),

    ) {
        Icon(
            imageVector = Icons.Filled.Phone,
//                painter = painterResource(SharedRes.images.icon_menu_main),
            contentDescription = "Filters icon",
            modifier = Modifier.size(15.dp),
            tint = Color.Black
        )

        Text(
            text = message.content
        )

    }
}

@Composable
fun MessageInput(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
) {
    var inputValue by remember { mutableStateOf("") }

    fun sendMessage() {
        viewModel.sendMessage(inputValue)
        inputValue = ""
    }

    Row(
        modifier.fillMaxWidth()
    ) {
//        TextField(
//            modifier = Modifier.weight(1f),
//            value = inputValue,
//            onValueChange = { inputValue = it },
////            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
////            keyboardActions = KeyboardActions { sendMessage() },
//        )
        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Email") }
        )

        Button(
            modifier = Modifier.height(56.dp),
            onClick = { sendMessage() },
            enabled = inputValue.isNotBlank(),
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "",
                modifier = Modifier.size(15.dp),
                tint = Color.Black
            )
        }
    }
}