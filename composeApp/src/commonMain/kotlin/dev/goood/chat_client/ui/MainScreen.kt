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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.LineAwesomeIcons
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.lineawesomeicons.PlusSquareSolid
import compose.icons.lineawesomeicons.QuestionCircle
import compose.icons.lineawesomeicons.TrashAlt
import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.ui.composable.AddChatDialog
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.ui.composable.DeleteChatDialog
import dev.goood.chat_client.viewModels.MainViewModel
import dev.goood.chat_client.viewModels.MainViewModel.State
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.gemini
import kotlinproject.composeapp.generated.resources.openai_
import kotlinproject.composeapp.generated.resources.test
import kotlinproject.composeapp.generated.resources.unknown
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    toChat: (chat: Chat) -> Unit,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {

    val viewModel = koinViewModel<MainViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val chats by viewModel.chats.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF))
    ) {
        Column (
            horizontalAlignment = Alignment.End,
//            horizontalArrangement = Arrangement.End,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 10.dp, end = 10.dp)
        ) {
            CButton(
                icon = LineAwesomeIcons.PlusSquareSolid,
                onClick = {
                    viewModel.addChatDialogState.value = true
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
//                            viewModel.deleteChatDialogState.value = true
                            },
                            onDeleteClick = { chatToDel ->
                                viewModel.deleteChatDialogState.value = chatToDel
                            }
                        )
                    }
                }
            }

            is State.Error -> {
                val error = (state as State.Error).message
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(error)
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
    if (deleteDialogState != null) {
        DeleteChatDialog(
            chat = deleteDialogState!!,
            onDismiss = { viewModel.deleteChatDialogState.value = null },
            onDelete = { chat ->
                viewModel.deleteChat(chat)
                viewModel.deleteChatDialogState.value = null
            },
        )
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
    Card (
        colors = CardDefaults.cardColors(Color(0xFFF5F5F5)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .clickable(enabled = true) {
                toChat(chat)
            }

    ) {
        Row (
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

            Row (
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
            Row(
                horizontalArrangement = Arrangement.End,
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



    }
}