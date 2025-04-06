package dev.goood.chat_client.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.goood.chat_client.di.appModulePreview
import dev.goood.chat_client.ui.ChatScreen
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

@Composable
@Preview(showSystemUi = true)
fun ChatScreenPreview() {
    val context = LocalContext.current
    val chatID = 1
    KoinApplication(application = {
        // If you need Context
        androidContext(context)
        modules(appModulePreview)
    }) {
        ChatScreen(chatID)
    }
}