package dev.goood.chat_client.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import dev.goood.chat_client.model.Chat


@Composable
internal fun DeleteChatDialog(
    chat: Chat,
    onDismiss: () -> Unit,
    onDelete: (chat: Chat) -> Unit,
    modifier: Modifier = Modifier
) {

    Dialog(
//        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,

        ){
        Surface(
            modifier = modifier.wrapContentSize()
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = modifier
                    .padding(all = 20.dp)
            ) {
                Text(
                    text = "Delete chat ${chat.name}?",
                    fontSize = 25.sp,
                    modifier = modifier
                        .padding(vertical = 10.dp)
                )

                Row {

                    CButton(
                        text = "Ok",
                        onClick = {
                            onDelete( chat )
                        },
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