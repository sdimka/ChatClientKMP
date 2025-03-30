package dev.goood.chat_client.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.goood.chat_client.viewModels.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {

    val viewModel = koinViewModel<MainViewModel>()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = viewModel.getData()
        )

        Button(
            onClick = { viewModel.getApiData() },
            content = {
                Text(text = "Get Data")
            }
        )
    }
}