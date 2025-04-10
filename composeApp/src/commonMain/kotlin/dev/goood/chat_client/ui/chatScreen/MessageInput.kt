package dev.goood.chat_client.ui.chatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.CogSolid
import compose.icons.lineawesomeicons.PaperPlane
import dev.goood.chat_client.ui.composable.buttonBackground
import dev.goood.chat_client.viewModels.ChatViewModel

@Composable
fun MessageInput(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
) {
    var inputValue by remember { mutableStateOf("") }
    var settingsState by remember { mutableStateOf(false) }

    fun sendMessage() {
        viewModel.sendMessage(inputValue)
        inputValue = ""
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier.fillMaxWidth().background(Color.White),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box {
                        TextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            label = { Text("Type your message...") },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = buttonBackground,
                                unfocusedIndicatorColor = buttonBackground,
                                focusedLabelColor = buttonBackground
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = LineAwesomeIcons.CogSolid,
                                    contentDescription = "",
                                    tint = Color.Black,
                                    modifier = modifier
                                        .clickable {
                                            settingsState = !settingsState
                                        }
                                        .size(25.dp),
                                )
                            },
                            modifier = modifier.fillMaxWidth()
                        )
                    }
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
                        imageVector = LineAwesomeIcons.PaperPlane,
                        contentDescription = "",
                        tint = Color.Black,
                        modifier = modifier.size(25.dp),
                    )
                }
            }

            if (settingsState) {
                SettingsElement()
            }
        }
    }
}

@Composable
fun SettingsElement(

){
    Column {
        Text(text = "Some")
        Text(
            text = "Some",
            modifier = Modifier.padding(top = 15.dp)
        )
    }
}