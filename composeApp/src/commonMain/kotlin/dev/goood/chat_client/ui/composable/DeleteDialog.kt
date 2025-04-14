package dev.goood.chat_client.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.goood.chat_client.model.Chat


@Composable
internal fun DeleteDialog(
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
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .wrapContentSize()
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

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.padding(vertical = 10.dp)
                ) {

                    CButton(
                        text = "Ok",
                        onClick = {
                            onDelete( chat )
                        },
                        modifier = modifier
                            .padding(bottom = 10.dp)
                            .padding(end = 15.dp)

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
fun <T> DeleteDialogImp(
    item: T,
    title: String = "Are you sure you want to delete this item?",
    getItemName: (T) -> String = { "" },
    onDismiss: () -> Unit,
    onDelete: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .wrapContentSize()
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = modifier.padding(20.dp)
            ) {
                Text(
                    text = "$title ${getItemName(item)}?",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    CButton(
                        text = "Ok",
                        onClick = { onDelete(item) },
                        modifier = Modifier
                            .padding(end = 15.dp)
                    )
                    CButton(
                        text = "Cancel",
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}