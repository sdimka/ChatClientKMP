package dev.goood.chat_client.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.LineAwesomeIcons
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.lineawesomeicons.PlusSquareSolid
import compose.icons.lineawesomeicons.QuestionCircle
import compose.icons.lineawesomeicons.TrashAlt
import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.model.ChatList
import dev.goood.chat_client.ui.chatScreen.ChatScreen
import dev.goood.chat_client.ui.composable.AddChatDialog
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.ui.composable.DeleteDialogImp
import dev.goood.chat_client.ui.composable.SwipeableWithActions
import dev.goood.chat_client.viewModels.MainViewModel
import dev.goood.chat_client.viewModels.MainViewModel.State
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.gemini
import kotlinproject.composeapp.generated.resources.openai_
import kotlinproject.composeapp.generated.resources.test
import kotlinproject.composeapp.generated.resources.unknown
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ChatListScreen(
    toChat: (chat: Chat) -> Unit,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {

    val viewModel = koinViewModel<MainViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val chats by viewModel.chats.collectAsStateWithLifecycle()

    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val scope = rememberCoroutineScope()

    val selectedChat = remember { mutableStateOf<Chat?>(null) }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            ListScaffold(
                state = state,
                chats = chats,
                onNewChat = { viewModel.addChatDialogState.value = true },
                onEdit = {  },
                onDelete = { chatToDel -> viewModel.deleteChatDialogState.value = chatToDel},
                toChat = {
                    selectedChat.value = it
                    scope.launch {
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    }
                },

                snackBarHostState = snackBarHostState,
            )
        },
        detailPane = {
            ChatScreen(
                chatID = selectedChat.value?.id ?: -1,
                modifier = Modifier,
                snackBarHostState = snackBarHostState
            )
        },
        extraPane = {  },
    )

    val dialogState by viewModel.addChatDialogState.collectAsState()
    if (dialogState) {
        AddChatDialog(
            onDismiss = { viewModel.addChatDialogState.value = false },
            onSaved = {
                viewModel.onNewChatSaved()
                viewModel.addChatDialogState.value = false
                      },
        )
    }

    val deleteDialogState by viewModel.deleteChatDialogState.collectAsState()
    deleteDialogState?.let { chatToDelete ->
        DeleteDialogImp(
            item = chatToDelete,
            title = "Delete chat",
            getItemName = { it.name },
            onDismiss = { viewModel.deleteChatDialogState.value = null },
            onDelete = { chat ->
                viewModel.deleteChat(chat)
                viewModel.deleteChatDialogState.value = null
            }
        )
    }
}

@Composable
fun ListScaffold(
    state: State,
    chats: ChatList,
    onNewChat: () -> Unit,
    toChat: (chat: Chat) -> Unit,
    onEdit: (chat: Chat) -> Unit,
    onDelete: (chat: Chat) -> Unit,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().background(Color(0xFFE7ECEF))
    ) {


        Column(
            horizontalAlignment = Alignment.End,
//            horizontalArrangement = Arrangement.End,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 10.dp, end = 10.dp)
        ) {
            CButton(
                icon = LineAwesomeIcons.PlusSquareSolid,
                onClick = {
                    onNewChat()
                }
            )
        }
        when (state) {
            is State.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 15.dp)
                ) {
                    items(chats) { chat ->
                        ChatElement(
                            chat = chat,
                            toChat = toChat,
                            onEditClick = {
                                onEdit(it)
//                            viewModel.deleteChatDialogState.value = true
                            },
                            onDeleteClick = { chatToDel ->
                                onDelete(chatToDel)
                            }
                        )
                    }
                }
            }

            is State.Error -> {
                val error = state.message
                LaunchedEffect(snackBarHostState, error) {
                    snackBarHostState.showSnackbar(
                        error,
                        actionLabel = "Close",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }

            State.Loading -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BallProgerssIndicator()
                }
            }
        }
    }
}


@Composable
fun ChatElement(
    chat: Chat,
    onEditClick: (chat: Chat) -> Unit = {},
    onDeleteClick: (chat: Chat) -> Unit = {},
    toChat: (chat: Chat) -> Unit,
    modifier: Modifier = Modifier,
) {
    val  resource: DrawableResource = when (chat.source.id) {
        1 -> Res.drawable.gemini
        2 -> Res.drawable.openai_
        4 -> Res.drawable.test
        else -> {
            Res.drawable.unknown
        }
    }
    SwipeableWithActions(
        isRevealed = false,
        actions = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = modifier.padding(end = 8.dp)
            ) {
                CButton(
                    icon = LineAwesomeIcons.QuestionCircle, //LineAwesomeIcons.PenFancySolid,
                    onClick = {
                        onEditClick(chat)
                    },
                    modifier = modifier.padding(end = 4.dp)
                )
                CButton(
                    icon = LineAwesomeIcons.TrashAlt,
                    onClick = {
                        onDeleteClick(chat)
                    },
                )
            }
        }
    ) {
        Card(
            colors = CardDefaults.cardColors(Color(0xFFF5F5F5)),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .clickable(enabled = true) {
                    toChat(chat)
                }

        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth().padding(8.dp),
            ) {
                Image(
                    painter = painterResource(
                        resource
                    ),
                    contentDescription = "Vector Image",
                    modifier = Modifier.size(45.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = modifier.padding(start = 10.dp)
                    ) {
                        Text(
                            text = chat.name,
                            fontSize = 16.sp,
                        )
                        Text(
                            text = chat.model.displayName,
                            fontSize = 12.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

            }


        }
    }
}