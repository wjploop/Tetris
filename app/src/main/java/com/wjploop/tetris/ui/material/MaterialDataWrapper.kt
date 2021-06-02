package com.wjploop.tetris.ui.material

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap

val LocalMaterial = compositionLocalOf {
    MaterialDataWrapper(ImageBitmap(1, 1))
}
data class MaterialDataWrapper(val bitmap: ImageBitmap)