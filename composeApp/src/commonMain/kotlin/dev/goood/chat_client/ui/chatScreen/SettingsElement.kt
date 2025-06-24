package dev.goood.chat_client.ui.chatScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.MinusCircleSolid
import compose.icons.lineawesomeicons.PlusCircleSolid
import dev.goood.chat_client.ui.composable.DropDownMenu
import dev.goood.chat_client.ui.filesDialog.FilesDialog
import dev.goood.chat_client.viewModels.ChatViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsElement(
    chatID: Int?,
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {

    val sysMessagesList by viewModel.systemMessages.collectAsStateWithLifecycle()
    val selectedSysMessage by viewModel.selectedSysMessage.collectAsStateWithLifecycle()
    val selectedFiles by viewModel.filesList.collectAsStateWithLifecycle()
    val isMessageEnabled by viewModel.isPreviousMessagesEnabled.collectAsStateWithLifecycle()

    val fileDialogState = remember { mutableStateOf(false) }

    Card(
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors().copy(containerColor = Color.White),
        modifier = modifier
//            .background(Color.White, shape = RoundedCornerShape(15.dp))
//            .border(border = BorderStroke(1.dp, Color.LightGray),)
            .padding(horizontal = 8.dp)
            .padding(bottom = 5.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(horizontal = 8.dp)
                .padding(top = 10.dp)
        ) {
            Text(
                text = "Attach previous massages:",
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 5.dp, end = 15.dp)
            )
            CompositionLocalProvider( // Remove paddings
                LocalMinimumInteractiveComponentSize provides 0.dp
            ) {
                Checkbox(
                    checked = isMessageEnabled,
                    onCheckedChange = viewModel::onPreviousMessagesEnabledChanged
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Text(
                text = "System prompt:",
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 5.dp)
            )
            DropDownMenu(
                itemList = sysMessagesList,
                selectedItem = selectedSysMessage,
                onSelected = {
                    viewModel.selectSysMessage(it)
                },
                itemLabel = { it.title },
                modifier = modifier
            )

        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
        ) {

            Text(
                text = "Files:",
                fontSize = 12.sp,
                modifier = modifier
                    .padding(vertical = 3.dp, horizontal = 5.dp)
                    .padding(end = 3.dp)
            )

            selectedFiles.forEach { file ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = modifier
                        .background(color = Color.LightGray, shape = CircleShape)
                        .padding(vertical = 3.dp, horizontal = 5.dp)
                ) {
                    Text(
                        file.filename,
                        fontSize = 12.sp,
                        modifier = modifier.padding(horizontal = 3.dp)
                    )

                    Icon(
                        imageVector = LineAwesomeIcons.MinusCircleSolid,
                        contentDescription = "",
                        tint = Color.Black,
                        modifier = modifier
                            .clickable {
                                viewModel.updateFileList(file) { list, f -> list - f }
                            }
                            .size(25.dp),
                    )
                }
            }

            Icon(
                imageVector = LineAwesomeIcons.PlusCircleSolid,
                contentDescription = "",
                tint = Color.Black,
                modifier = modifier
                    .background(color = Color.LightGray, shape = CircleShape)
                    .padding(vertical = 3.dp, horizontal = 5.dp)
                    .clickable {
                        fileDialogState.value = !fileDialogState.value
                    }
                    .size(25.dp),
            )
        }


    }

    if (fileDialogState.value) {
        FilesDialog(
            chatID = chatID,
            selectedFilesList = viewModel.filesList,
            selectedFilesListUpdate = viewModel::updateFileList,
            onDismiss = { fileDialogState.value = false }
        )
    }
}