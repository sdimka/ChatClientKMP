package dev.goood.chat_client.ui.composable


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.goood.chat_client.ui.theme.buttonBackground
import dev.goood.chat_client.ui.theme.grayBackground
import dev.goood.chat_client.viewModels.AddChatViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
internal fun AddChatDialog(
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel = koinViewModel<AddChatViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    val sourceList by viewModel.sourceList.collectAsStateWithLifecycle()
    val modelList by viewModel.modelList.collectAsStateWithLifecycle()
    val chatName by viewModel.chatName.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()

    LaunchedEffect(LocalLifecycleOwner.current) {
        viewModel.upDate()
    }


    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = modifier
                .wrapContentSize()
                .widthIn(max = 500.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                .background(Color.White, RoundedCornerShape(24.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
            ) {
                // Header section with title
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(
                            buttonBackground.copy(alpha = 0.1f),
                            RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(vertical = 24.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create New Chat",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = buttonBackground
                    )
                }

                HorizontalDivider(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                // Content section
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Chat name field
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Chat Name",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray,
                            modifier = modifier.padding(start = 4.dp)
                        )
                        MTextFiled(
                            value = chatName,
                            onValueChange = { viewModel.setChatName(it) },
                            modifier = modifier.fillMaxWidth()
                        )
                    }

                    // Source selection
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Source",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray,
                            modifier = modifier.padding(start = 4.dp)
                        )
                        SegmentedButtons(
                            choiceList = sourceList,
                            onSelected = {
                                viewModel.setSelectedSource(it)
                            },
                            modifier = modifier.fillMaxWidth()
                        )
                    }

                    // Model selection
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Model",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray,
                            modifier = modifier.padding(start = 4.dp)
                        )
                        DropDownMenu(
                            itemList = modelList,
                            selectedItem = selectedModel,
                            onSelected = { viewModel.setSelectedModel(it) },
                            itemLabel = { it.displayName },
                            modifier = modifier.fillMaxWidth()
                        )
                    }
                }

                HorizontalDivider(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                // Action buttons section
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.DarkGray
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    CButton(
                        text = "Create",
                        onClick = {
                            viewModel.createNewChat()
                            onSaved()
                        },
                        enabled = state.value is AddChatViewModel.State.FormValid,
                        modifier = modifier
                            .weight(1f)
                            .height(44.dp)
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
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = {
            Text(
                text = "Enter chat name...",
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 15.sp
            )
        },
        textStyle = TextStyle(
            color = Color.DarkGray,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = grayBackground,
            focusedBorderColor = buttonBackground,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            focusedTextColor = Color.DarkGray,
            unfocusedTextColor = Color.DarkGray,
            cursorColor = buttonBackground
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun AddChatDialogPreview(){
    AddChatDialog(
        onDismiss = {},
        onSaved = {},

    )
}