package dev.goood.chat_client.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.MarkdownTypography

val defaultTextSize = 14.sp

@Composable
fun defaultMarkDownTypography(): MarkdownTypography {
    return markdownTypography(
        h1 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
        h2 = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
        h3 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
        h4 = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        text = TextStyle(fontSize = 14.sp)
    )
}