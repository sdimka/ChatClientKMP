package dev.goood.chat_client.previews

import androidx.compose.runtime.Composable
import dev.goood.chat_client.ui.LoginScreen
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview(showSystemUi = true)
fun MyComposablePreview() {
    LoginScreen(onLogin = {})
}