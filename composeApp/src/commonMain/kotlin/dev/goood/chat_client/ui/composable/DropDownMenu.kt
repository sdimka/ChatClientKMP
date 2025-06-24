package dev.goood.chat_client.ui.composable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.ArrowDownSolid
import dev.goood.chat_client.model.ChatModel



val itemListL = listOf(
    ChatModel(
        id = 1,
        name = "Model 1",
        displayName = "Model dName",
        description = "Some long description",
        sourceID = 1
    ),
    ChatModel(
        id = 2,
        name = "Model 1",
        displayName = "Model dName",
        description = "Some long description",
        sourceID = 1
    ),
    ChatModel(
        id = 3,
        name = "Model 1",
        displayName = "Model dName",
        description = "Some long description",
        sourceID = 1
    ),
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropDownMenu(
    itemList: List<T>,
    selectedItem: T?,
    onSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {

                DropDownTextField(
                    value = selectedItem?.let { itemLabel(it) } ?: "Select item",
                    label = "Select item",
                    expanded = expanded,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    itemList.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = itemLabel(item),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            },
                            onClick = {
                                onSelected(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownTextField(
    value: String,
    label: String,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val colors =
        OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color.DarkGray
        )

    BasicTextField(
        modifier = modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth(),
        value = value.ifEmpty { label },
        onValueChange = { },
        readOnly = true,
        // textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start)
        singleLine = true,
        maxLines = 1,

    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value.ifEmpty { label },
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            singleLine = true,
            enabled = false,
            interactionSource = interactionSource,
            contentPadding =
                OutlinedTextFieldDefaults.contentPadding(top = 5.dp, bottom = 5.dp),
            trailingIcon = {
                Icon(
                    imageVector = LineAwesomeIcons.ArrowDownSolid,
                    contentDescription = "Dropdown icon",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 5.dp)
                        .graphicsLayer( // Example: Rotate the icon
                            rotationZ = if (expanded) 180f else 0f
                        )
                )
            },
            container = {
                OutlinedTextFieldDefaults.Container(
                    enabled = false,
                    isError = false,
                    colors = colors,
                    interactionSource = interactionSource,
                    shape = RoundedCornerShape(10.dp),
                    unfocusedBorderThickness = 1.dp,
                    focusedBorderThickness = 1.dp,
//                            modifier = Modifier.weight(0.5f)
                )
            }
        )
    }
}