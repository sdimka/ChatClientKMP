package dev.goood.chat_client.previews

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.goood.chat_client.di.appModulePreview
import dev.goood.chat_client.ui.MainScreen
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

@Composable
@Preview(showSystemUi = true)
fun MainScreenPreview() {
    val context = LocalContext.current

    val snackBarHostState = remember { SnackbarHostState() }

    KoinApplication(application = {
        // If you need Context
        androidContext(context)
        modules(appModulePreview)
    }) {
        MainScreen(
            toChat = {},
            snackBarHostState = snackBarHostState,
        )
    }
}