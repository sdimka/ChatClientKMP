package dev.goood.chat_client.ui.systemMessages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.ui.composable.BallProgerssIndicator
import dev.goood.chat_client.viewModels.SystemMessagesViewModel
import dev.goood.chat_client.viewModels.SystemMessagesViewModel.State
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SystemMessagesScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<SystemMessagesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF))
    ) {
        when (state) {
            is State.Error -> {

            }
            State.Loading -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BallProgerssIndicator()
                }
            }
            State.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 15.dp)
                ) {
                    items(messages) { message ->
                        SysMessElement(
                            sysMessage = message,
                            onEdit = {

                            },
                            onDelete = {

                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SysMessElement(
    sysMessage: SystemMessage,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp),
    ){
        Text(
            text = sysMessage.title
        )
    }
}