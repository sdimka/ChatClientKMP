package dev.goood.chat_client.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.goood.chat_client.ui.LoginScreen
import androidx.compose.ui.tooling.preview.Preview
import dev.goood.chat_client.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

@Composable
@Preview(showSystemUi = true)
fun MyComposablePreview() {
    val context = LocalContext.current

    KoinApplication(application = {
        // If you need Context
        androidContext(context)
        modules(appModule)
    }) {
        LoginScreen(onLogin = {})
    }
}