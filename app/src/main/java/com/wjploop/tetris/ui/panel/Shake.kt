package com.wjploop.tetris.ui.panel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import com.wjploop.tetris.ext.logx
import com.wjploop.tetris.ui.material.ratio
import kotlinx.coroutines.delay
import java.lang.Math.PI
import kotlin.math.sin


// 摇晃屏幕
@Composable
fun Shake(shake: Boolean, child: @Composable () -> Unit) {

    val y: Float by animateFloatAsState(
        targetValue = if (shake) 1f else 0f, animationSpec = tween(
            durationMillis = 100, delayMillis = 0
        ) {
            //
            (sin(it * PI / 2) * ratio).toFloat()
        }
    )

    Box(
        modifier = Modifier.graphicsLayer(
            translationY = y
        )
    ) {
        child()
    }

}
