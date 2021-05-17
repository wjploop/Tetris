package com.wjploop.tetris.ui.material

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
private fun Brick(color: Color, size: Size = LocalBrickSize.current.size) {
    val width = size.width.dp
    BoxWithConstraints(
        Modifier
            .size(width)
            .padding(all = width * 0.05f)
            .border(width = width * 0.1f, color = color)
            .padding(width * 0.1f),
        contentAlignment = Alignment.Center

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = color)
        )
    }
}

@Composable
fun Brick(brickType: BrickType) {
    when (brickType) {
        BrickType.empty -> Brick(color = Color(0x1F000000))
        BrickType.normal -> Brick(color = Color(0xDD000000))
        BrickType.hilight -> Brick(color = Color(0xFF560000))
    }
}

enum class BrickType {
    normal,
    empty,
    hilight
}

data class BrickSize(val size: Size)

val LocalBrickSize = staticCompositionLocalOf<BrickSize> { error("no brick size provided") }


@Preview(backgroundColor = 0xff9ead86, showBackground = true)
@Composable
fun PreViewBrick() {
    Column(Modifier.padding(10.dp)) {
        BrickType.values().forEach {
            Brick(brickType = it)
        }
    }
}

