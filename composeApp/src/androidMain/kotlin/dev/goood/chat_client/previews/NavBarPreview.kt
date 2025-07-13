package dev.goood.chat_client.previews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.HeadsetSolid
import compose.icons.lineawesomeicons.HourglassEndSolid
import compose.icons.lineawesomeicons.ListSolid
import dev.goood.chat_client.NavigationRoute
import dev.goood.chat_client.ui.BottomRouteElement
import dev.goood.chat_client.ui.composable.BottomNavBar

@Preview(showBackground = true)
@Composable
fun PreviewBottomNavBar() {
    val items = listOf(
        BottomRouteElement("Chats", LineAwesomeIcons.ListSolid, NavigationRoute.MainGraph),
        BottomRouteElement("SysMessages", LineAwesomeIcons.HourglassEndSolid, NavigationRoute.SystemMessagesGraph),
        BottomRouteElement("Translate", LineAwesomeIcons.HeadsetSolid, NavigationRoute.TranslateRoute)
    )
    var currentSelectedItem by remember { mutableStateOf(items[0]) } // Simulate internal state for preview

    BottomNavBar(
        navItems = items,
        currentSelectedItem = currentSelectedItem,
        onItemSelected = { item ->
            currentSelectedItem = item
        }
    )
}