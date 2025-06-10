package dev.goood.chat_client.ui.chatScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.CogSolid
import compose.icons.lineawesomeicons.FileAltSolid
import compose.icons.lineawesomeicons.InfoCircleSolid
import compose.icons.lineawesomeicons.PaperPlane
import dev.goood.chat_client.ui.theme.buttonBackground
import dev.goood.chat_client.ui.theme.green
import dev.goood.chat_client.viewModels.ChatViewModel

@Composable
fun MessageInput(
    chatID: Int?,
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
) {
    val inputValue by viewModel.inputValue.collectAsStateWithLifecycle()
    var settingsVisible by remember { mutableStateOf(true) }
    val systemMessage by viewModel.selectedSysMessage.collectAsStateWithLifecycle()
    val filesList by viewModel.filesList.collectAsStateWithLifecycle()
    val isFileListEmpty = filesList.isEmpty()

    fun sendMessage() {
        viewModel.sendMessage(inputValue)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column {

            AnimatedVisibility(
                visible = settingsVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                SettingsElement(
                    chatID = chatID,
                    viewModel = viewModel
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = modifier
                            .background(Color.LightGray)
                    ) {
                        TextField(
                            value = inputValue,
                            onValueChange = { newVal ->
                                viewModel.updateInputValue(newVal) },
                            label = { Text("Type your message...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.LightGray,
                                unfocusedContainerColor = Color.LightGray,
                                focusedIndicatorColor = buttonBackground,
                                unfocusedIndicatorColor = buttonBackground,
                                focusedLabelColor = buttonBackground
                            ),
                            leadingIcon = {
                                if (systemMessage != null) {
                                    Icon(
                                        imageVector = LineAwesomeIcons.InfoCircleSolid,
                                        contentDescription = "",
                                        tint = green,
                                        modifier = modifier
                                            .align(Alignment.TopEnd)
                                            .padding(all = 5.dp)
                                            .size(15.dp),
                                    )
                                }

                                if (!isFileListEmpty) {
                                    Icon(
                                        imageVector = LineAwesomeIcons.FileAltSolid,
                                        contentDescription = "",
                                        tint = Color.Red,
                                        modifier = modifier
                                            .align(Alignment.TopEnd)
                                            .padding(all = 5.dp)
                                            .padding(end = 20.dp)
                                            .size(15.dp),
                                    )
                                }
                            },
                            trailingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = LineAwesomeIcons.CogSolid,
                                        contentDescription = "",
                                        tint = Color.Black,
                                        modifier = modifier
                                            .padding(end = 8.dp)
                                            .clickable {
                                                settingsVisible = !settingsVisible
                                            }
                                            .size(25.dp),
                                    )

                                    Button(
                                        shape = RoundedCornerShape(5.dp),
                                        onClick = {
                                            settingsVisible = false
                                            sendMessage()
                                        },
                                        colors = ButtonDefaults.buttonColors(Color(0xFF274C77)),
                                        modifier = modifier.padding(end = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = LineAwesomeIcons.PaperPlane,
                                            contentDescription = "",
                                            tint = Color.LightGray,
                                            modifier = modifier.size(25.dp),
                                        )
                                    }
                                }
                            },
                            modifier = modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}