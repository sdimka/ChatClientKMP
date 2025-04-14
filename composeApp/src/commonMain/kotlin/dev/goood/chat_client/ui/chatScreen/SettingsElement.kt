package dev.goood.chat_client.ui.chatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.goood.chat_client.ui.composable.DropDownMenu
import dev.goood.chat_client.ui.theme.grayBackground
import dev.goood.chat_client.viewModels.ChatViewModel


@Composable
fun SettingsElement(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {

    val sysMessagesList by viewModel.systemMessages.collectAsStateWithLifecycle()
    val selectedSysMessage by viewModel.selectedSysMessage.collectAsStateWithLifecycle()

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
    }
}