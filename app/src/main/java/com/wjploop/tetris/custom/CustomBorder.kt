package com.wjploop.tetris.custom

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp


data class BorderColorFourDirection(
    val top: Color,
    val right: Color,
    val bottom: Color,
    val left: Color,
)

/**
 *  实现一个矩形的边框，而边框由四个边组成，四个边可以颜色不同
 *
 *  每一边实际上是一个等腰梯形，而非矩形
 *  若是矩形，在边角处效果很烂，边角处会重叠
 *  若是4个边颜色相同，则可以直接画矩形了
 *
 *  边框颜色相同时，不该用该实现
 */
fun Modifier.borderWithDifferentColor(width: Dp, borderColors: BorderColorFourDirection): Modifier =
    composed(
        factory = {
            this.then(
                Modifier.drawWithCache {
                    val strokeWidth = width.toPx()

                    val rect = Rect(Offset.Zero, size)
                    onDrawWithContent {
                        drawContent()

                        val path = Path()

                        path.reset()
                        path.moveTo(rect.left, rect.top)
                        path.lineTo(rect.right, rect.top)
                        path.lineTo(rect.right - strokeWidth, rect.top + strokeWidth)
                        path.lineTo(rect.left + strokeWidth, rect.top + strokeWidth);
                        drawPath(path = path, borderColors.top)

                        path.reset()
                        path.moveTo(rect.right, rect.top)
                        path.lineTo(rect.right, rect.bottom)
                        path.lineTo(rect.right - strokeWidth, rect.bottom - strokeWidth)
                        path.lineTo(rect.right - strokeWidth, rect.top + strokeWidth);
                        drawPath(path = path, borderColors.right)

                        path.reset()
                        path.moveTo(rect.left, rect.bottom)
                        path.lineTo(rect.right, rect.bottom)
                        path.lineTo(rect.right - strokeWidth, rect.bottom - strokeWidth)
                        path.lineTo(rect.left + strokeWidth, rect.bottom - strokeWidth);
                        drawPath(path = path, borderColors.bottom)

                        path.reset()
                        path.moveTo(rect.left, rect.top)
                        path.lineTo(rect.top, rect.bottom)
                        path.lineTo(rect.left + strokeWidth, rect.bottom - strokeWidth)
                        path.lineTo(rect.left + strokeWidth, rect.top + strokeWidth);
                        drawPath(path = path, borderColors.left)


                        // stroke
                        // 画四根线，还不如直接画一个矩形呢
                        // 将Paint.StrokeType 设为边框宽度即可,
//
//                        drawLine(
//                            SolidColor(borderColors.left),
//                            Offset.Zero,
//                            Offset(0f, size.height),
//                            strokeWidth = stroke.width,
//                            cap = StrokeCap.Square,
//                        )
//                        drawLine(
//                            SolidColor(borderColors.right),
//                            Offset(size.width, 0f),
//                            Offset(size.width, size.height),
//                            strokeWidth = stroke.width,
//                            cap = StrokeCap.Square,
//                        )
//                        drawLine(
//                            SolidColor(borderColors.bottom),
//                            Offset(0f, size.height),
//                            Offset(size.width, size.height),
//                            strokeWidth = stroke.width,
//                            cap = StrokeCap.Square,
//                        )
                    }
                }
            )
        },
    )


// Hairline stroke to cover aliasing of clipping
private val HairlineBorderStroke = Stroke(Stroke.HairlineWidth)