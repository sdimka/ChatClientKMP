package dev.goood.chat_client.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.goood.chat_client.di.appModule
import dev.goood.chat_client.ui.SettingsScreen
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication


@Composable
@Preview(showSystemUi = true)
fun  SettingsScreenPreview() {
    val context = LocalContext.current

    KoinApplication(application = {
        // If you need Context
        androidContext(context)
        modules(appModule)
    }) {
        SettingsScreen(
            onLogout = {}
        )
    }
}