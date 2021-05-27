package com.wjploop.tetris.ui.panel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.wjploop.tetris.R

val digital_raw_size = IntSize(14, 24)

// todo 了解dp
const val ratio = 2


@ExperimentalStdlibApi
@Composable
fun GamerNumber(number: Int, length: Int = 5, padWidthZero: Boolean = false) {

    var numberStr = number.toString()
    if (numberStr.length > length) {
        numberStr = numberStr.substring(numberStr.length - length)
    }
    numberStr = numberStr.padStart(length = length, if (padWidthZero) '0' else ' ')
    Row {
        repeat(length) {
            Digital(digital = numberStr[it].digitToIntOrNull(10))
        }
    }

}

@Composable
fun Material(
    size: IntSize,
    srcOffset: IntOffset,
    srcSize: IntSize,
    materialDataWrapper: MaterialDataWrapper = LocalMaterial.current
) {
    //  todo 每个数字都加载一次资源不太好，上升该图片公用
//    val context = LocalContext.current
//    val bitmap = remember {
//        BitmapFactory.decodeStream(context.assets.open("material.png")).asImageBitmap()
//    }
//    val bitmap = ImageBitmap.imageResource(id = R.drawable.material)

    val bitmap = materialDataWrapper.bitmap
    // 注意过程中可能修改srcOffset要重新composition
    val bitmapPainter = remember(srcOffset, size) {
        object : Painter() {
            override val intrinsicSize: Size
                get() = size.toSize()

            override fun DrawScope.onDraw() {

                drawImage(
                    bitmap,
                    srcOffset = srcOffset,
                    srcSize = srcSize,
                    dstSize = size
                )
            }
        }

    }
    Image(painter = bitmapPainter, null)
}

// size 单位似乎没找到好的办法，暂时写死一个比列
@Composable
fun Digital(digital: Int?, size: IntSize = IntSize(10, 17) * ratio) {
    val index = digital ?: 10
    val srcOffsetDx = 75 + 14 * index
    val srcOffset = IntOffset(srcOffsetDx, 25)
    Material(size = size, srcOffset = srcOffset, srcSize = digital_raw_size)
}


@Composable
fun IconSound(enable: Boolean, size: IntSize = IntSize(25, 16) * ratio) {
    Material(
        size = size,
        srcOffset = if (enable) IntOffset(150, 75) else IntOffset(175, 75),
        srcSize = IntSize(25, 21)
    )
}

@Composable
fun IconColon(enable: Boolean, size: IntSize = IntSize(10, 17) * ratio) {
    Material(
        size = size,
        srcOffset = if (enable) IntOffset(229, 25) else IntOffset(243, 25),
        srcSize = digital_raw_size
    )
}

@Composable
fun IconPause(enable: Boolean, size: IntSize = IntSize(18, 16) * ratio) {
    Material(
        size = size,
        srcOffset = if (enable) IntOffset(75, 75) else IntOffset(100, 75),
        srcSize = IntSize(20, 18)
    )
}

@Preview
@Composable
fun PreviewIconPause() {
    IconPause(enable = false)
}

@Preview
@Composable
fun PreViewIconColon() {
    IconColon(enable = true)
}

@Preview
@Composable
fun PreViewIconSound() {
    IconSound(enable = true)
}


@ExperimentalStdlibApi
@Preview
@Composable
fun PreViewNumber() {

    Digital(digital = 1)
}

@ExperimentalStdlibApi
@Preview
@Composable
fun PreViewGameNumber() {
    GamerNumber(number = 123, padWidthZero = false)
}
