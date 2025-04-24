package dev.goood.chat_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.goood.chat_client.ui.composable.CButton


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {

        // Box 1: 20% height
        Box(
            modifier = Modifier // Start a new modifier chain for this specific Box
                .fillMaxWidth()
                .weight(0.20f) // 20% of parent height
                .background(Color.Yellow)
        )

        // Box 2: 35% height
        Box(
            modifier = Modifier // New chain
                .fillMaxWidth()
                .weight(0.35f) // 35% of parent height
                .background(Color.Green)
        )

        // Box 3: 45% height
        Box(
            modifier = Modifier // New chain
                .fillMaxWidth()
                .weight(0.45f) // 45% of parent height
                .background(Color.LightGray)
        )
    }
}