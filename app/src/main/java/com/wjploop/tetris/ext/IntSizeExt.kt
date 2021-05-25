package com.wjploop.tetris.ext

import androidx.compose.ui.unit.IntSize

operator fun IntSize.times(other: Float): IntSize {
    return IntSize((this.width * other).toInt(), (height * other).toInt())
}