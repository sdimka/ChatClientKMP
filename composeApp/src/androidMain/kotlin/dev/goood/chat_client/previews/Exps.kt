package dev.goood.chat_client.previews


import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import dev.goood.chat_client.previews.AnchoredDraggableSampleValue.Center
import dev.goood.chat_client.previews.AnchoredDraggableSampleValue.End
import dev.goood.chat_client.previews.AnchoredDraggableSampleValue.HalfEnd
import dev.goood.chat_client.previews.AnchoredDraggableSampleValue.HalfStart
import dev.goood.chat_client.previews.AnchoredDraggableSampleValue.Start
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private enum class AnchoredDraggableSampleValue {
    Start,
    HalfStart,
    Center,
    HalfEnd,
    End,
}

@Composable
@Preview
fun AnchoredDraggableAnchorsFromCompositionSample() {
    val density = LocalDensity.current

    val state =
        rememberSaveable(saver = AnchoredDraggableState.Saver()) {
            AnchoredDraggableState(initialValue = Center)
        }
    
    val draggableWidth = 70.dp
    val containerWidthPx = with(density) { draggableWidth.toPx() }
    // Our anchors depend on the density which is obtained from composition, so we update them using
    // updateAnchors whenever they are available
    SideEffect {
        state.updateAnchors(
            DraggableAnchors {
                Start at 0f
                Center at containerWidthPx / 2f
                End at containerWidthPx
            }
        )
    }

    Box(Modifier.width(draggableWidth)) {
        Box(
            Modifier.size(100.dp)
                .offset { IntOffset(x = state.requireOffset().roundToInt(), y = 0) }
                .anchoredDraggable(
                    state,
                    Orientation.Horizontal,
                    flingBehavior =
                        AnchoredDraggableDefaults.flingBehavior(
                            state,
                            positionalThreshold = { distance -> distance * 0.25f },
                        ),
                )
                .background(Color.Red)
        )
    }
}