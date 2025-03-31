package dev.goood.chat_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.viewModels.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {

    val viewModel = koinViewModel<MainViewModel>()

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

        Box(
            modifier = Modifier.height(100.dp).fillMaxWidth().background(Color.White)
        )
    }
}