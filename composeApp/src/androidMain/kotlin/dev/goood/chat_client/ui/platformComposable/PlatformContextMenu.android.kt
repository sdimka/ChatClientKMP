package dev.goood.chat_client.ui.platformComposable

import android.util.Log
import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

class PopupTextToolbar(
    private val onRequestShow: (Rect) -> Unit,
    private val onRequestHide: () -> Unit
) : TextToolbar {

    private var lastShownRect: Rect? = null
    private var isShown = false
    // Added a flag to track if the last hide was user-initiated
    private var userInitiatedHide = false

    override val status: TextToolbarStatus
        get() = if (isShown) TextToolbarStatus.Shown else TextToolbarStatus.Hidden

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        Log.d("Some", "showMenu: On show, isShown=$isShown, lastShownRect=$lastShownRect, rect=$rect, userInitiatedHide=$userInitiatedHide")

        // If the previous hide was user-initiated, ignore this immediate show request
        if (userInitiatedHide) {
            userInitiatedHide = false // Reset the flag
            Log.d("Some", "showMenu: Ignoring immediate show after user hide.")
            return // Do not show the menu
        }

        // Otherwise, proceed with showing/updating the menu based on rect change
        if (lastShownRect != rect) {
            lastShownRect = rect
            isShown = true // Menu is now shown
            onRequestShow(rect)
        }
        // If lastShownRect == rect, the menu is already shown at the same position,
        // so we don't need to do anything.
    }

    // Added a parameter to indicate if the hide was user-initiated
    fun hide(userInitiated: Boolean = false) {
        Log.d("Some", "hide: On hide, isShown=$isShown, userInitiated=$userInitiated")
        if (isShown) {
            isShown = false
            lastShownRect = null // Clear the last shown rect when hiding
            this.userInitiatedHide = userInitiated // Set the flag
            onRequestHide()
        }
        // If isShown is already false, do nothing.
    }

    // Override the default hide() method from TextToolbar,
    // calling our internal hide with userInitiated=false
    override fun hide() {
        hide(userInitiated = false)
    }
}

@Composable
actual fun PlatformContextMenu(
    selectedTextProvider: () -> String,
    content: @Composable () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var menuRect by remember { mutableStateOf(Rect.Zero) } // Note: DropdownMenu doesn't use this rect for positioning directly

    val toolbar = remember {
        PopupTextToolbar(
            onRequestShow = { rect ->
                menuRect = rect // Update rect state if needed, though DropdownMenu doesn't use it
                showMenu = true
            },
            onRequestHide = {
                showMenu = false
            }
        )
    }

    CompositionLocalProvider(LocalTextToolbar provides toolbar) {
        Box {
            content()

            DropdownMenu(
                expanded = showMenu,
                // When dismissed by clicking outside or back button
                onDismissRequest = {
                    Log.d("Some", "onDismissRequest: Dismissing menu")
                    showMenu = false
                    toolbar.hide(userInitiated = true) // Call hide with userInitiated = true
                }
            ) {
                DropdownMenuItem(
                    text = { Text("Translate") },
                    onClick = {
                        Log.d("Some", "Translate tapped")
                        // Handle translate logic here...
                        showMenu = false // Hide the menu
                        toolbar.hide(userInitiated = true) // Call hide with userInitiated = true
                    }
                )
                DropdownMenuItem(
                    text = { Text("Share") },
                    onClick = {
                        Log.d("Some", "Share tapped")
                        // Handle share logic here...
                        showMenu = false // Hide the menu
                        toolbar.hide(userInitiated = true) // Call hide with userInitiated = true
                    }
                )
            }
        }
    }
}