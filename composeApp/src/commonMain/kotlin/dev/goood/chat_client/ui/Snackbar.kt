package dev.goood.chat_client.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.PhoneSlashSolid
import kotlinx.coroutines.launch



@Composable
fun ScaffoldSnackbar() {
    // [START android_compose_layout_material_snackbar]
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show snackbar") },
                icon = { Icon(LineAwesomeIcons.PhoneSlashSolid, contentDescription = "") },
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Snackbar")
                    }
                }
            )
        }
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            Text("Screen content")
        }
        // [END_EXCLUDE]
    }
    // [END android_compose_layout_material_snackbar]
}