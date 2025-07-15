package dev.goood.chat_client.ui.chatScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.Connectdevelop
import compose.icons.lineawesomeicons.Copy
import compose.icons.lineawesomeicons.EllipsisHSolid
import compose.icons.lineawesomeicons.InfoSolid
import compose.icons.lineawesomeicons.Save
import compose.icons.lineawesomeicons.TrashAlt
import compose.icons.lineawesomeicons.User
import compose.icons.lineawesomeicons.UserNinjaSolid
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.DeleteDialogImp
import dev.goood.chat_client.ui.theme.defaultMarkDownTypography
import dev.goood.chat_client.ui.theme.defaultTextSize
import dev.goood.chat_client.viewModels.ChatViewModel
import dev.goood.chat_client.viewModels.ChatViewModel.State
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ChatScreen(
    chatID: Int?,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState
) {

    val viewModel: ChatViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val deleteDialogState = remember { mutableStateOf<Message?>(null) }

    LaunchedEffect(chatID) {
        val id = chatID ?: return@LaunchedEffect
        viewModel.resetChatSpecificStates()
        viewModel.getMessages(id)
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = modifier
            .fillMaxSize()
//            .background(Color.Cyan)
    ) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            MessageList(
                viewModel = viewModel,
                onDelete = { message: Message ->
                    deleteDialogState.value = message
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            MessageInput(
                chatID = chatID,
                viewModel = viewModel,
//            modifier = Modifier.fillMaxWidth()
            )
        }


        when (state) {
            is State.Error -> {
                val error = (state as State.Error).message
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        error,
                        actionLabel = "Close",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }

            State.Loading -> {
                Box(
                    modifier = modifier.fillMaxSize()
                        .background(Color.LightGray.copy(alpha = 0.5f))
                ) {
                    BallProgerssIndicator(
                        modifier = modifier.align(Alignment.Center)
                    )
                }
            }

            State.NewReply -> {}
            State.Success -> {}
        }
    }

    deleteDialogState.value?.let { messageToDelete ->
        DeleteDialogImp(
            item = messageToDelete,
            title = "Delete message",
            getItemName = { it.id.toString() },
            onDismiss = { deleteDialogState.value = null },
            onDelete = { message ->
                viewModel.deleteMessage(message)
                deleteDialogState.value = null
            }
        )
    }
}

@Composable
fun MessageList(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
    onDelete: (Message) -> Unit,
) {

    val listState = rememberLazyListState()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val newReply = viewModel.newReply.collectAsStateWithLifecycle()
    val isSelectedEnabled by viewModel.isPreviousMessagesEnabled.collectAsStateWithLifecycle()

    val messagesList by viewModel.messages.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    fun copyContent(text: String) {
        clipboardManager.setText(AnnotatedString(text))
    }

    LaunchedEffect(messagesList, state) {
        if (messagesList.isNotEmpty() || state.value is State.NewReply) {
            listState.animateScrollToItem(index = 0)
        }
    }

    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier
            .padding(5.dp)
    ) {
        if (state.value is State.NewReply) {
            item {
                NewMessageElement(newReply.value)
            }
        }

        items(messagesList, key = { it.id }) { message ->
            MessageElement(
                message = message,
                onToClipboard = { copyContent(message.content) },
                onDelete = onDelete,
                isSelectedEnabled = isSelectedEnabled,
                onSelect = viewModel::onSelectedMessagesListUpdate

            )
        }
    }
}

@Composable
fun MessageElement(
    message: Message,
    onToClipboard: () -> Unit,
    onDelete: (Message) -> Unit,
    isSelectedEnabled: Boolean = false,
    onSelect: (messageID: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (message.initiator == 0) LineAwesomeIcons.User else LineAwesomeIcons.UserNinjaSolid
    val bColor = if (message.initiator == 0) Color(0xFF6096BA) else Color(0xFFA3CEF1)
    val text = if (message.initiator == 0) "User" else "Assistant"

    Card(
        border = BorderStroke(1.dp, bColor),
        colors = CardDefaults.cardColors().copy(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .background(bColor)  // Green color for header
                    .padding(horizontal = 16.dp)
//                    .padding(vertical = 3.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "User icon",
                    modifier = modifier.size(22.dp),
                    tint = Color.Black
                )

                Text(
                    text = text,
                    modifier = modifier.padding(start = 5.dp).padding(end = 10.dp)
                )

                if (message.systemMessage != null) {
                    Icon(
                        imageVector = LineAwesomeIcons.InfoSolid,
                        contentDescription = "Sys icon",
                        modifier = modifier.size(18.dp),
//                        tint = green
                    )
                    Text(
                        text = message.systemMessage.title,
                        fontSize = defaultTextSize,
//                        color = green
                    )
                }

                if (message.files?.isNotEmpty() == true) {
                    Icon(
                        imageVector = LineAwesomeIcons.Save,
                        contentDescription = "Files icon",
                        modifier = modifier
                            .padding(start = 15.dp)
                            .size(18.dp),
                        tint = Color.DarkGray
                    )
                    Text(
                        text = "[${message.files.first().name}]",
                        fontSize = defaultTextSize,
                    )
                }

                Spacer(modifier.weight(1f))

                DropDownMenuButton(
                    onSelected = onToClipboard,
                    onDelete = {
                        onDelete(message)
                    }
                )

                if (isSelectedEnabled) {
                    CompositionLocalProvider( // Remove paddings
                        LocalMinimumInteractiveComponentSize provides 0.dp
                    ) {
                        Checkbox(
                            checked = message.isSelected,
                            onCheckedChange = {
//                                isSelected.value = !isSelected.value
                                onSelect(message.id)
                            }
                        )
                    }
                }
            }
//        PlatformContextMenu(
//            selectedTextProvider = {
//                ""
//            }
//        ) {
            SelectionContainer(

            ) {
                Markdown(
                    content = message.content,
                    colors = markdownColor(text = Color.Black),
                    typography = defaultMarkDownTypography(),
                    modifier = modifier
                        .padding(horizontal = 8.dp)
                        .padding(vertical = 8.dp)
                )
            }
//        }
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
                imageVector = LineAwesomeIcons.Connectdevelop,
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
fun DropDownMenuButton(
    onSelected: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .height(36.dp)
                .width(36.dp)
                .clip(RoundedCornerShape(4.dp))
//                .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(4.dp))
                .clickable { expanded = !expanded },
        ) {

            Icon(
                imageVector = LineAwesomeIcons.EllipsisHSolid,
                contentDescription = "Dropdown icon",
                modifier = Modifier.align(Alignment.Center)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = LineAwesomeIcons.Copy,
                                contentDescription = "Sys icon",
                                modifier = modifier.size(25.dp),
                                tint = Color.DarkGray
                            )
                            Text("Copy clear text")
                        }
                    },
                    onClick = {
                        onSelected()
                        expanded = false
                    }
                )

                HorizontalDivider()

                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = LineAwesomeIcons.TrashAlt,
                                contentDescription = "Sys icon",
                                modifier = modifier.size(25.dp),
                                tint = Color.Red
                            )
                            Text(
                                text = "Delete",
                                color = Color.Red
                            )
                        }
                    },
                    onClick = {
                        onDelete()
                        expanded = false
                    }
                )

            }
        }
    }
}