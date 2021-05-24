package com.wjploop.tetris.ui.panel

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.wjploop.tetris.R
import kotlin.math.roundToInt

val digital_raw_size = IntSize(14, 24)

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
// size 单位似乎没找到好的办法，暂时写死一个比列
fun Digital(digital: Int?, size: IntSize = IntSize((10 * 3.0).toInt(), (17 * 3.0).toInt())) {

    //  todo 每个数字都加载一次资源不太好，上升该图片公用
    val context = LocalContext.current
//    val bitmap = remember {
//        BitmapFactory.decodeStream(context.assets.open("material.png")).asImageBitmap()
//    }
    val bitmap = ImageBitmap.imageResource(id = R.drawable.material)
    val index = digital ?: 10
    val srcOffsetDx = 75 + 14 * index
    val srcOffset = IntOffset(srcOffsetDx, 25)

    val bitmapPainter = remember {
        object : Painter() {
            override val intrinsicSize: Size
                get() = size.toSize()


            override fun DrawScope.onDraw() {

                drawImage(
                    bitmap,
                    srcOffset = srcOffset,
                    srcSize = digital_raw_size,
                    dstSize = size
                )
            }
        }

    }
    Image(painter = bitmapPainter, null)
}


@ExperimentalStdlibApi
@Preview
@Composable
fun PreViewNumber() {

    Digital(digital = 1)
}

@Preview
@Composable
fun PreViewGameNumber() {
    GamerNumber(number = 123, padWidthZero = false)
}
