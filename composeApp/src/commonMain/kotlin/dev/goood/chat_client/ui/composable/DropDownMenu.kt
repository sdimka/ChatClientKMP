package dev.goood.chat_client.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.goood.chat_client.model.ChatModel



val itemListL = listOf(
    ChatModel(
        id = 1,
        name = "Model 1",
        displayName = "Model dName",
        description = "Some long description"
    ),
    ChatModel(
        id = 2,
        name = "Model 1",
        displayName = "Model dName",
        description = "Some long description"
    ),
    ChatModel(
        id = 3,
        name = "Model 1",
        displayName = "Model dName",
        description = "Some long description"
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    itemList: List<ChatModel> = itemListL,
    onSelected: (ChatModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(ChatModel(1, "Choose model", "", "")) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier
//                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterHorizontally)
                .width(250.dp)
                .clip(RoundedCornerShape(10.dp)),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextField(
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    value = selectedItem.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
        //                    leadingIcon = {
        //                        Icon(
        //                            imageVector = selectedItem.icon,
        //                            contentDescription = selectedItem.title
        //                        )
        //                    }
                    )

            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                itemList.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
//                                Icon(
//                                    imageVector = item.icon,
//                                    contentDescription = item.title
//                                )
                                Text(item.name)
                            }
                        },
                        onClick = {
                            selectedItem = itemList[index]
                            onSelected(selectedItem)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Text(
            text = "${selectedItem.name}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 20.dp)
        )

    }
}