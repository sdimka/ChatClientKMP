package dev.goood.chat_client


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import dev.goood.chat_client.di.createKoinConfiguration
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinMultiplatformApplication


@Composable
@Preview
fun App() {

    KoinMultiplatformApplication(
        config = createKoinConfiguration()
    ) {
        MaterialTheme {

            AppScreen()

        }
    }
}