package dev.goood.chat_client.previews


import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.goood.chat_client.ui.LoginScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview // now we are able to call the preview annotation
fun MyComposablePreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    LoginScreen(onLoginSuccess = {}, snackbarHostState)
}