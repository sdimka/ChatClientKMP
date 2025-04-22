package dev.goood.chat_client.previews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun CustomCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
//        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
//                .padding(16.dp)
        ) {
            // Card Header with Highlighted Background and Specific Border Shape
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)  // Height of the header
                    .background(Color(0xFF4CAF50))  // Green color for header
                    .padding(horizontal = 16.dp)
                    .clip(MaterialTheme.shapes.medium.copy(topEnd = CornerSize(16.dp), topStart = CornerSize(16.dp)))
            ) {
                Text(
                    text = "Improvement in English",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                )
            }

            // Card Content (Main Body)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You are the best in the world at creating prompts. Refactor the given prompt in the attached file. Also, change the part where the user provides a draft proposal, but ensure that the final document includes points that have improved their syntactic structure and style.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    color = Color.Black
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewCustomCard() {
    CustomCard()
}