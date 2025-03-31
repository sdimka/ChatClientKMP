package dev.goood.chat_client.ui.composable

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
* Colors:
* #75DDDD
* 0xFF166EB4
* #172A3A
* #336C6F
* #09BC8A
*
*  gray bg - #EFEFEF
* */

@Composable
fun CButton(
    text: String = "",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFF166EB4)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}