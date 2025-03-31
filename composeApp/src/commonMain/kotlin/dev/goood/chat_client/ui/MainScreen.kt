package dev.goood.chat_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.viewModels.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {

    val viewModel = koinViewModel<MainViewModel>()
    val reqString = viewModel.reqString.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF))
    ) {
        Text(
            text = viewModel.getData()
        )

        CButton(
            text = "Get Data",
            onClick = { viewModel.getApiData() },
        )

        CButton(
            text = "Get Data List",
            onClick = { viewModel.getApiList() },
        )

        CButton(
            text = "Get Stream",
            onClick = { viewModel.getStreamString() },
        )

        Text(
            text = reqString.value
        )

    }
}