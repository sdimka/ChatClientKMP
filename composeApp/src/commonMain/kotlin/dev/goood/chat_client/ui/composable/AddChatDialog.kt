package dev.goood.chat_client.ui.composable


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
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

    LaunchedEffect(LocalLifecycleOwner.current) {
        viewModel.upDate()
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,

    ){
        Surface(
            shape = RoundedCornerShape(16.dp),
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

                MTextFiled(
                    value = chatName,
                    onValueChange = { viewModel.setChatName(it) },
//                    label = { Text("Chat name") },
                    modifier = modifier.padding(bottom = 10.dp)

                )

                SegmentedButtons(
                    choiceList = sourceList,
                    onSelected = {
                        viewModel.setSelectedSource(it)
                    },
                    modifier = modifier
                        .padding(vertical = 20.dp)
                        .padding(horizontal = 10.dp),
                )

                DropDownMenu(
                    itemList = modelList,
                    onSelected = {
                        viewModel.setSelectedModel(it)
                    },
                    modifier = modifier.fillMaxWidth().padding(bottom = 10.dp),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.padding(top = 15.dp)
                ) {
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

@Composable
fun MTextFiled(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.LightGray)
    ) {
        Text(
            text = "Title:",
            modifier = modifier.padding(start = 5.dp)
        )

        BasicTextField(
            value = value,
            singleLine = true,
            textStyle = TextStyle(
                color = Color.DarkGray,
                fontFamily = FontFamily.Companion.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            ),

//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.Text,
//            capitalization = KeyboardCapitalization.None,
//            autoCorrect = false,
//            imeAction = ImeAction.Search
//        ),

//        keyboardActions = KeyboardActions(
//            onSearch = {
//                onExecuteSearch()
//            }
//        ),

            onValueChange = {
                onValueChange(it)
            },

            modifier = modifier
                .fillMaxWidth()
                .padding(all = 5.dp)
//                .height(30.dp)
        )
    }
}