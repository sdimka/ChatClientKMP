package dev.goood.chat_client.ui.systemMessages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.PlusSquareSolid
import compose.icons.lineawesomeicons.TrashAlt
import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.viewModels.SystemMessagesViewModel
import dev.goood.chat_client.services.SystemMessagesService.State
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.ui.composable.DeleteDialog
import dev.goood.chat_client.ui.composable.SwipeableWithActions
import dev.goood.chat_client.ui.theme.grayBackground
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SystemMessagesScreen(
    modifier: Modifier = Modifier,
    toDetail: (Int) -> Unit,
    toNew: () -> Unit,
    snackBarHostState: SnackbarHostState
) {
    val viewModel = koinViewModel<SystemMessagesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .background(grayBackground)
    ) {

        CButton(
            icon = LineAwesomeIcons.PlusSquareSolid,
            onClick = {
                toNew()
            },
            modifier = modifier
                .padding(top = 10.dp).padding(end = 10.dp)
                .align(Alignment.End)
        )

        when (state) {
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
            State.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 15.dp)
                ) {
                    items(messages) { message ->
                        SysMessElement(
                            sysMessage = message,
                            onEdit = toDetail,
                            onDelete = {
//                                viewModel.deleteMessage(it)
                            }
                        )
                    }
                }
            }
        }
    }

//    var deleteDialogState by remember { mutableStateOf(false) }
//    if (deleteDialogState) {
//        DeleteDialog(
//            chat = deleteDialogState!!,
//            onDismiss = { deleteDialogState = !deleteDialogState },
//            onDelete = { chat ->
//
//            },
//        )
//    }
}

@Composable
fun SysMessElement(
    sysMessage: SystemMessage,
    onEdit: (messageID: Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    SwipeableWithActions(
        isRevealed = false,
        actions = {
            CButton(
                icon = LineAwesomeIcons.TrashAlt,
                onClick = {
                    onDelete(sysMessage.id)
                },
                modifier = Modifier.fillMaxHeight().padding(5.dp)
            )
        }
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clickable {
                    onEdit(sysMessage.id)
                },
        ) {
            Row(
                modifier = modifier
                    .padding(10.dp)

            ) {
                Text(
                    text = sysMessage.title
                )
            }
        }
    }
}