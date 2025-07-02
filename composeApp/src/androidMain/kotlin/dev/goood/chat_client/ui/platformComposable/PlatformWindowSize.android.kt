package dev.goood.chat_client.ui.platformComposable

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun PlatformWindowSize(): WindowSizeClass {
    val activity: Activity? = LocalActivity.current

    return if (activity != null) {
        calculateWindowSizeClass(activity)
    } else {
        WindowSizeClass.calculateFromSize(
            size = DpSize(800.dp, 600.dp),
            supportedWidthSizeClasses = WindowWidthSizeClass.DefaultSizeClasses,
            supportedHeightSizeClasses = WindowHeightSizeClass.DefaultSizeClasses
        )
    }

}