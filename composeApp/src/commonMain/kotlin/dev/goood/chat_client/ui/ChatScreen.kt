package dev.goood.chat_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.User
import compose.icons.lineawesomeicons.UserNinjaSolid
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.buttonBackground
import dev.goood.chat_client.ui.composable.verticalColumnScrollbar
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

    var inputValue by remember { mutableStateOf("") }

    LaunchedEffect(LocalLifecycleOwner.current) {
        if (chatID != null) {
            viewModel.getMessages(chatID)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MessageList(
            viewModel = viewModel,
        )

        Spacer(modifier = Modifier.padding(bottom = 120.dp))
    }

    MessageInput(
        viewModel = viewModel,
    )

}

@Composable
fun MessageList(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
) {

    val scrollState = rememberScrollState()
    val state = viewModel.state.collectAsState()
    val newReply = viewModel.newReply.collectAsState()

    val messagesList by viewModel.messages.collectAsState()

    LazyColumn(
        reverseLayout = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .padding(bottom = 60.dp)
    ) {
        if (state.value is ChatViewModel.State.NewReply) {
            item {
                NewMessageElement(newReply.value)
            }
        }

        items(messagesList) { message ->
            MessageElement(message = message)
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 120.dp))
}


@Composable
fun MessageElement(
    message: Message,
    onDeleteClick: (message: Message) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val icon = if (message.initiator == 0) LineAwesomeIcons.User else LineAwesomeIcons.UserNinjaSolid
    val innPadding = if (message.initiator == 0) 0.dp else 5.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .padding(start = innPadding),

        ) {
        Row(
            modifier = modifier.padding(horizontal = 5.dp).padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
//                painter = painterResource(SharedRes.images.icon_menu_main),
                contentDescription = "Filters icon",
                modifier = modifier.size(18.dp),
                tint = Color.Black
            )

            Text(
                text = "${message.initiator}",
                modifier = modifier.padding(start = 10.dp)
            )

        }
        SelectionContainer {
            Text(
                text = message.content,
                modifier = modifier.padding(horizontal = 8.dp).padding(bottom = 5.dp)
            )
        }

    }
}

@Composable
fun NewMessageElement(
    message: String,
    modifier: Modifier = Modifier
) {
    Card  (
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp),

        ) {
        Row (
            modifier = modifier.padding(horizontal = 5.dp).padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Phone,
//                painter = painterResource(SharedRes.images.icon_menu_main),
                contentDescription = "Filters icon",
                modifier = modifier.size(15.dp),
                tint = Color.Black
            )

            Text (
                text = "1",
                modifier = modifier.padding(start = 10.dp)
            )

            BallProgerssIndicator(
                modifier = modifier.padding(start = 100.dp)
            )
        }

        Text(
            text = message,
            modifier = modifier.padding(horizontal = 5.dp).padding(bottom = 5.dp)
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier.fillMaxWidth().background(Color.White),
        ) {
            Column(modifier = Modifier.weight(1f)) {

                TextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text("Type your message...") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = buttonBackground,
                        unfocusedIndicatorColor = buttonBackground,
                        focusedLabelColor = buttonBackground
                    ),
                    modifier = modifier.fillMaxWidth()
                )
            }
            Button(
                shape = RectangleShape,
                onClick = {
                    sendMessage()
                },
                colors = ButtonDefaults.buttonColors(buttonBackground),
                modifier = modifier
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "",
                    tint = Color.Black,
                    modifier = modifier.size(15.dp),
                )
            }
        }
    }
}