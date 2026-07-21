package dev.goood.chat_client


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import dev.goood.chat_client.di.createKoinConfiguration
import org.koin.compose.KoinApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.logger.Level


@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {

    KoinApplication(configuration = createKoinConfiguration(), logLevel = Level.INFO, content = {
            MaterialTheme {

                AppScreen()

            }
        })
}