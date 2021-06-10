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
import com.wjploop.tetris.custom.BorderColorFourDirection
import com.wjploop.tetris.custom.borderWithDifferentColor

val SCREEN_BORDER_WIDTH = 12.0.dp

@Composable
fun ScreenDecoration(child: @Composable () -> Unit) {

    BoxWithConstraints(
        Modifier
            // 实现边框是，左上是一种颜色，右下是另一个颜色，以达到一种内嵌屏幕的视觉效果
//            .border(width = SCREEN_BORDER_WIDTH, color = Color(0xFFfae36c))
            .borderWithDifferentColor(width = SCREEN_BORDER_WIDTH, BorderColorFourDirection(
                left= Color(0xFF987f0f),
                top= Color(0xFF987f0f),
                right= Color(0xFFfae36c),
                bottom= Color(0xFFfae36c),
            ))
            .padding(3.dp)
            .background(SCREEN_BACKGROUND)
    ) {
        child()
    }

}