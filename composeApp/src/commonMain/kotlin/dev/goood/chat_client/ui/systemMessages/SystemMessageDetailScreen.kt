package dev.goood.chat_client.ui.systemMessages

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.goood.chat_client.viewModels.SMDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SystemMessageDetailScreen(
    messID: Int?,
    snackBarHostState: SnackbarHostState
) {

    val viewModel = koinViewModel<SMDetailViewModel>()
    val currentMessage by viewModel.selectedMessage.collectAsStateWithLifecycle()

    LaunchedEffect(LocalLifecycleOwner.current) {
        println("Current message ${messID}")
        if (messID != null) {
            viewModel.setCurrentMessage(messID)
        }
    }

    Column(

    ) {
        if (currentMessage != null) {
            Text(
                text = currentMessage!!.id.toString()
            )

            Text(
                text = currentMessage!!.title
            )

            Text(
                text = currentMessage!!.content
            )
        }
    }

}