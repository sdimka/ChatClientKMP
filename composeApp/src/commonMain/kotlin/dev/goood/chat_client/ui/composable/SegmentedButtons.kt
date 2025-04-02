package dev.goood.chat_client.ui.composable

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun SegmentedButtons(
    modifier: Modifier = Modifier
) {
    val choices = remember { mutableStateListOf("Good", "Better", "Best") }
    var selectedChoiceIndex = remember { mutableIntStateOf(0) }

    val darkBackgroundColor = Color.DarkGray

    SingleChoiceSegmentedButtonRow(
        modifier = modifier
    ) {
        choices.forEachIndexed { index, choice ->

            SegmentedButton(
                selected = selectedChoiceIndex.intValue == index,
                onClick = { selectedChoiceIndex.intValue = index },
                colors = SegmentedButtonDefaults.colors(
                    activeBorderColor = darkBackgroundColor,
                    activeContainerColor = buttonBackground,
                    activeContentColor = Color.White,
                    inactiveContentColor = Color.White,
                    inactiveBorderColor = darkBackgroundColor,
                    inactiveContainerColor = darkBackgroundColor
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = choices.count()
                )
            ) {
                Text(choice)
            }
        }
    }
}