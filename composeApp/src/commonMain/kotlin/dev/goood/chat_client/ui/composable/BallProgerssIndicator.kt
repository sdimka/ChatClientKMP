package dev.goood.chat_client.ui.composable

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

private const val DefaultAnimationDuration = 700

private const val DefaultMinAlpha = .5f
private const val DefaultMaxAlpha = 1f
private const val DefaultBallCount = 3

private val DefaultMinBallDiameter = 8.dp
private val DefaultMaxBallDiameter = 14.dp
private val DefaultSpacing = 4.dp

@Composable
fun BallProgerssIndicator(
    modifier: Modifier = Modifier,
    color: Color = buttonBackground,
    animationDuration: Int = DefaultAnimationDuration,
    ballCount: Int = DefaultBallCount,
    minAlpha: Float = DefaultMinAlpha,
    maxAlpha: Float = DefaultMaxAlpha,
    minBallDiameter: Dp = DefaultMinBallDiameter,
    maxBallDiameter: Dp = DefaultMaxBallDiameter,
    spacing: Dp = DefaultSpacing
) {
    val transition = rememberInfiniteTransition()

    val fraction by transition.fraction2StepReversed(animationDuration)

    val maxWidth = maxBallDiameter * ballCount + spacing * (ballCount - 1)

    ProgressIndicator(modifier, maxWidth, maxBallDiameter) {
        val diameter = listOf(
            lerp(minBallDiameter, maxBallDiameter, fraction).toPx(),
            lerp(minBallDiameter, maxBallDiameter, 1 - fraction).toPx()
        )

        val alpha = listOf(
            androidx.compose.ui.util.lerp(minAlpha, maxAlpha, fraction),
            androidx.compose.ui.util.lerp(minAlpha, maxAlpha, 1 - fraction)
        )

        drawIndeterminateBallBeatIndicator(
            maxDiameter = maxBallDiameter.toPx(),
            diameter = diameter,
            ballCount = ballCount,
            spacing = spacing.toPx(),
            color = color,
            alpha = alpha,
        )
    }
}

private fun DrawScope.drawIndeterminateBallBeatIndicator(
    maxDiameter: Float,
    diameter: List<Float>,
    ballCount: Int,
    spacing: Float,
    color: Color,
    alpha: List<Float>
) {
    val offset = maxDiameter + spacing

    for (i in 0 until ballCount) {
        val x = maxDiameter / 2 + offset * i
        drawCircle(
            color = color,
            radius = diameter[i % 2] / 2,
            center = Offset(x, size.height / 2),
            alpha = alpha[i % 2]
        )
    }
}

@Composable
internal fun ProgressIndicator(
    modifier: Modifier,
    size: Dp,
    onDraw: DrawScope.() -> Unit
) {
    Canvas(
        modifier = modifier
            .progressSemantics()
            .size(size)
            .focusable(),
        onDraw = onDraw
    )
}

@Composable
internal fun ProgressIndicator(
    modifier: Modifier,
    width: Dp,
    height: Dp,
    onDraw: DrawScope.() -> Unit
) {
    Canvas(
        modifier = modifier
            .progressSemantics()
            .size(width, height)
            .focusable(),
        onDraw = onDraw
    )
}

@Composable
internal fun InfiniteTransition.fraction2StepReversed(
    durationMillis: Int,
    delayMillis: Int = 0,
    easing: Easing = LinearEasing
): State<Float> {
    val duration = durationMillis + delayMillis

    return animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                this.durationMillis = duration
                0f at delayMillis / 2 using easing
                1f at duration / 2
                1f at (duration + delayMillis) / 2 using easing
                0f at duration
            }
        )
    )
}