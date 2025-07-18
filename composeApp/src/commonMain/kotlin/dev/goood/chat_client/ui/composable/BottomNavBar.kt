package dev.goood.chat_client.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.goood.chat_client.ui.BottomRouteElement

@Composable
fun BottomNavBar(
    navItems: List<BottomRouteElement>,
    currentSelectedItem: BottomRouteElement?,
    modifier: Modifier = Modifier,
    onItemSelected: (item: BottomRouteElement) -> Unit,
    navigationBarColor: Color = Color.LightGray,
    shape: Shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    iconSize: Dp = 24.dp,
    fontSize: TextUnit = 12.sp,
    iconBackgroundPadding: Dp = 8.dp,
    itemTint: Color = Color(0xFF6B6B6B),
    selectedItemTint: Color = Color.White,
    backgroundTint: Color = Color.Transparent,
    selectedBackgroundTint: Color = Color(0xFF4C6DED),
    selectedItemOffset: Dp = 5.dp
) {

    Box(
        modifier = modifier
            .height(56.dp)
            .background(navigationBarColor, shape),
    ) {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navItems.forEach { item ->

                val isSelected = currentSelectedItem == item

                BottomNavBarItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemSelected(item) },
                    iconSize = iconSize,
                    fontSize = fontSize,
                    iconBackgroundPadding = iconBackgroundPadding,
                    itemTint = itemTint,
                    selectedItemTint = selectedItemTint,
                    backgroundTint = backgroundTint,
                    selectedBackgroundTint = selectedBackgroundTint,
                    selectedItemOffset = selectedItemOffset,
                )
            }
        }
    }
}

@Composable
private fun BottomNavBarItem(
    item: BottomRouteElement,
    isSelected: Boolean,
    onClick: () -> Unit,
    iconSize: Dp,
    fontSize: TextUnit,
    iconBackgroundPadding: Dp,
    itemTint: Color,
    selectedItemTint: Color,
    backgroundTint: Color,
    selectedBackgroundTint: Color,
    selectedItemOffset: Dp,
) {

    val yOffset by animateDpAsState(
        targetValue = if (isSelected) -selectedItemOffset else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "itemYOffset"
    )

    Box(
        modifier = Modifier
            .width(80.dp)
            .clickable(
                onClick = onClick,
                indication = ripple(bounded = true),
                interactionSource = remember { MutableInteractionSource() } // Required for ripple
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .offset(y = yOffset),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) selectedBackgroundTint else backgroundTint)
                    .padding(iconBackgroundPadding)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(iconSize),
                    tint = if (isSelected) selectedItemTint else itemTint
                )
            }

            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = item.title,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Visible,
//                    modifier = Modifier.padding(top = 4.dp),
                    color = itemTint,
                    fontSize = fontSize
                )
            }
        }
    }
}