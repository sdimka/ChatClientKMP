package dev.goood.chat_client.ui.chatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.MinusCircleSolid
import compose.icons.lineawesomeicons.PlusCircleSolid
import dev.goood.chat_client.ui.composable.DropDownMenu
import dev.goood.chat_client.ui.filesDialog.FilesDialog
import dev.goood.chat_client.ui.theme.grayBackground
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

    val fileDialogState = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(grayBackground)
            .padding(all = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "System prompt:",
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
                .padding(7.dp)
        ) {

            Text(
                text = "Files:",
                modifier = modifier
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