package dev.goood.chat_client.ui.composable


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.goood.chat_client.model.Chat
import dev.goood.chat_client.model.ChatModel
import dev.goood.chat_client.model.ChatSource
import dev.goood.chat_client.viewModels.AddChatViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
internal fun AddChatDialog(
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel = koinViewModel<AddChatViewModel>()
    val state = viewModel.state.collectAsState()

    val sourceList by viewModel.sourceList.collectAsState()
    val modelList by viewModel.modelList.collectAsState()
    val chatName by viewModel.chatName.collectAsState()

//    var source by remember { mutableStateOf<ChatSource?>(null) }
//    var model by remember { mutableStateOf<ChatModel?>(null) }

    LaunchedEffect(LocalLifecycleOwner.current) {
        viewModel.upDate()
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,

    ){
        Surface(
            modifier = modifier.fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "Add new chat",
                    fontSize = 25.sp,
                    modifier = modifier
                        .height(50.dp)
                        .padding(vertical = 10.dp)
                )

                OutlinedTextField(
                    value = chatName,
                    onValueChange = { viewModel.setChatName(it) },
                    label = { Text("Chat name") },
                    modifier = modifier.padding(bottom = 10.dp)

                )

                SegmentedButtons(
                    choiceList = sourceList,
                    onSelected = {
                        viewModel.setSelectedSource(it)
                    },
                    modifier = modifier.padding(vertical = 20.dp),
                )

                DropDownMenu(
                    itemList = modelList,
                    onSelected = {
                        viewModel.setSelectedModel(it)
                    },
                    modifier = modifier.padding(bottom = 10.dp),
                )

                Row {
                    CButton(
                        text = "Ok",
                        onClick = {
                            viewModel.createNewChat()
                            onSaved()
                                  },
                        enabled = state.value is AddChatViewModel.State.FormValid,
                        modifier = modifier.padding(bottom = 10.dp)

                    )
                    CButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = modifier.padding(bottom = 10.dp)

                    )
                }
            }
        }
    }
}