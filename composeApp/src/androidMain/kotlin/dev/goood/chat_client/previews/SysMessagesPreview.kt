package dev.goood.chat_client.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.goood.chat_client.di.appModulePreview
import dev.goood.chat_client.ui.systemMessages.SystemMessagesScreen
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

@Composable
@Preview(showSystemUi = true)
fun SystemMessageScreenPreview() {
    val context = LocalContext.current

    KoinApplication(application = {
        // If you need Context
        androidContext(context)
        modules(appModulePreview)
    }) {
        SystemMessagesScreen(
//            chatID,
//            snackBarHostState = snackBarHostState
        )
    }
}