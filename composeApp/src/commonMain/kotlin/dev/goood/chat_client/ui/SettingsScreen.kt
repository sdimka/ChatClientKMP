package dev.goood.chat_client.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.goood.chat_client.ui.composable.CButton


@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {

//    val viewModel: TranslateViewModel = koinViewModel()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(top = 10.dp)

    ) {

        CButton(
            text = "Logout",
            onClick = {
                onLogout()
            },
        )


    }
}