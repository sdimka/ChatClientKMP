package dev.goood.chat_client.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.goood.chat_client.viewModels.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {

    val viewModel = koinViewModel<MainViewModel>()

    Column {
        Text(
            text = viewModel.getData()
        )
    }
}