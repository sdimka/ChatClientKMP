package dev.goood.chat_client.ui.chatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.composables.core.Menu
import com.composables.core.MenuButton
import com.composables.core.MenuContent
import com.composables.core.MenuItem
import com.composables.core.rememberMenuState
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.User
import compose.icons.lineawesomeicons.UserNinjaSolid
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.defaultTextSize
import dev.goood.chat_client.viewModels.ChatViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ChatScreen(
    chatID: Int?,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState
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
            modifier = modifier
                .padding(start = 10.dp)
                .padding(end = 5.dp)
                .padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
//                painter = painterResource(SharedRes.images.icon_menu_main),
                contentDescription = "Filters icon",
                modifier = modifier.size(22.dp),
                tint = Color.Black
            )

            Text(
                text = "${message.initiator}",
                modifier = modifier.padding(start = 10.dp)
            )
            Spacer(modifier.weight(1f))
            MenuElement()

        }
        SelectionContainer {
            Markdown(
                content = message.content,
                colors = markdownColor(text = Color.Black),
                typography = markdownTypography(h1 = TextStyle()),
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 5.dp)
            )

//            Text(
//                text = message.content,
//                fontSize = defaultTextSize,
//                modifier = modifier
//                    .padding(horizontal = 8.dp)
//                    .padding(bottom = 5.dp)
//            )
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
            modifier = modifier
                .padding(start = 10.dp)
                .padding(end = 5.dp)
                .padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Phone,
//                painter = painterResource(SharedRes.images.icon_menu_main),
                contentDescription = "Filters icon",
                modifier = modifier.size(22.dp),
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
            fontSize = defaultTextSize,
            modifier = modifier
                .padding(horizontal = 5.dp)
                .padding(bottom = 5.dp)
        )

    }
}

@Composable
fun MenuElement() {
    val options = listOf("United States", "Greece", "Indonesia", "United Kingdom")
    var selected by remember { mutableStateOf(0) }
    val state = rememberMenuState(expanded = false)

    Menu(
        state,
        modifier = Modifier
    ) {
        MenuButton(
            Modifier.clip(RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.ArrowDropDown, "contentDescription",

                )
            }
        }

        MenuContent(
            modifier = Modifier
                .width(320.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(4.dp)
        ) {
            options.forEachIndexed { index, option ->
                MenuItem(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                    onClick = { selected = index }
                ) {
                    BasicText(option,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 4.dp))
                }
            }
        }
    }
}