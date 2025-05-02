package dev.goood.chat_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.goood.chat_client.ui.composable.CButton
import dev.goood.chat_client.ui.theme.buttonBackground
import dev.goood.chat_client.viewModels.TranslateViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {

    val viewModel: TranslateViewModel = koinViewModel()

    var inputValue by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {

        // Box 1: 20% height
        Box(
            modifier = Modifier // Start a new modifier chain for this specific Box
                .fillMaxWidth()
                .weight(0.20f) // 20% of parent height
                .background(Color.Yellow)
        )

        // Box 2: 35% height
        Box(
            modifier = Modifier // New chain
                .fillMaxWidth()
                .weight(0.35f) // 35% of parent height
                .background(Color.Green)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth()
            ) {
                TextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text("") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = buttonBackground,
                        unfocusedIndicatorColor = buttonBackground,
                        focusedLabelColor = buttonBackground
                    ),
                    trailingIcon = {
                        CButton(
                            text = "Send",
                            onClick = { viewModel.translate() }
                        )
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                        .padding(vertical = 10.dp)
                )


            }
        }

        // Box 3: 45% height
        Box(
            modifier = Modifier // New chain
                .fillMaxWidth()
                .weight(0.45f) // 45% of parent height
                .background(Color.LightGray)
        )
    }
}