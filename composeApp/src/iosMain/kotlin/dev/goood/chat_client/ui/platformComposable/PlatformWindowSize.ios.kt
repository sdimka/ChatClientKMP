package dev.goood.chat_client.ui.platformComposable

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun PlatformWindowSize(): WindowSizeClass {
    val windowSizeClass = calculateWindowSizeClass()
    return windowSizeClass
}