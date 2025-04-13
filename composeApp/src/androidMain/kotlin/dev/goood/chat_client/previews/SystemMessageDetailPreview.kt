package dev.goood.chat_client.previews

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.goood.chat_client.di.appModulePreview
import dev.goood.chat_client.ui.systemMessages.SystemMessageDetailScreen
import androidx.compose.ui.tooling.preview.Preview
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication


@Composable
@Preview(showSystemUi = true)
fun SystemMessageDetailScreenPreview() {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    KoinApplication(application = {
        androidContext(context)
        modules(appModulePreview)
    }) {
        SystemMessageDetailScreen(
            messID = 1,
            snackBarHostState = snackBarHostState
        )
    }
}

