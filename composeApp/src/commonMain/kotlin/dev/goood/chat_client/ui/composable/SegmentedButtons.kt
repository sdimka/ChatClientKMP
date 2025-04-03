package dev.goood.chat_client.ui.composable

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.goood.chat_client.model.ChatSource
import dev.goood.chat_client.model.ChatSourceList


@Composable
fun SegmentedButtons(
    choiceList: ChatSourceList,
    onSelected: (ChatSource) -> Unit,
    modifier: Modifier = Modifier
) {

    val selectedChoiceIndex = remember { mutableIntStateOf(10) }

    val darkBackgroundColor = Color.DarkGray

    SingleChoiceSegmentedButtonRow(
        space = 5.dp,
        modifier = modifier
    ) {
        choiceList.forEachIndexed { index, choice ->

            SegmentedButton(
                selected = selectedChoiceIndex.intValue == index,
                onClick = {
                    selectedChoiceIndex.intValue = index
                    onSelected(choice)
                          },
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
                    count = choiceList.count()
                )
            ) {
                Text(choice.name)
            }
        }
    }
}