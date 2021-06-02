package com.wjploop.tetris.ui.panel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

const val SCREEN_BORDER_WIDTH = 3.0;

@Composable
fun ScreenDecoration(child: @Composable () -> Unit) {

    BoxWithConstraints(
        Modifier
            // todo 预期的边框是，左上是一种颜色，右下是另一个颜色，以达到一种内嵌屏幕的视觉效果 在compose要自己实现
            .border(width = SCREEN_BORDER_WIDTH.dp, color = Color(0xFFfae36c))
            .padding(3.dp)
            .background(SCREEN_BACKGROUND)
    ) {
        child()
    }

}