package com.wjploop.tetris.ui.panel

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.wjploop.tetris.R
import com.wjploop.tetris.ext.logx
import com.wjploop.tetris.ui.gamer.Gamer
import com.wjploop.tetris.ui.gamer.LocalGamer
import kotlinx.coroutines.*

// 模拟按钮边缘阴影
private val button_shadow_stroke_width = 6.dp

private val direction_button_size = 48.dp + button_shadow_stroke_width * 2
private val system_button_size = 28.dp + button_shadow_stroke_width * 2
private val spacer_height = 8.dp + button_shadow_stroke_width * 2


@Composable
fun GameController() {
    Box(Modifier.height(200.dp)) {
        Row {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                LeftController()
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                DirectionController()
            }
        }
    }
}

@Composable
fun DirectionController(gamer: Gamer = LocalGamer.current) {
    Box(
        Modifier.size(direction_button_size * 3),
    ) {
        GameActionButton(Modifier.align(Alignment.TopCenter), onAction = { gamer.rotate() })
        GameActionButton(Modifier.align(Alignment.CenterEnd), onAction = { gamer.right() })
        GameActionButton(Modifier.align(Alignment.BottomCenter), onAction = { gamer.down(true) })
        GameActionButton(Modifier.align(Alignment.CenterStart), onAction = { gamer.left() })

        Box(
            Modifier
                .size(direction_button_size)
                .align(Alignment.Center)
        ) {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "",
                Modifier
                    .size(direction_button_size / 2)
                    .align(Alignment.TopCenter)
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "",
                Modifier
                    .size(direction_button_size / 2)
                    .align(Alignment.CenterEnd)
                    .rotate(90f)
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "",
                Modifier
                    .size(direction_button_size / 2)
                    .align(Alignment.BottomCenter)
                    .rotate(180f)
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "",
                Modifier
                    .size(direction_button_size / 2)
                    .align(Alignment.CenterStart)
                    .rotate(270f)
            )
        }
    }
}


@Composable
fun LeftController(gamer: Gamer = LocalGamer.current) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GameActionButton(size = system_button_size, color = Color(0xFF2dc421), onAction = {
                    gamer.sounds()
                })
                Spacer(Modifier.height(spacer_height))
                Text(stringResource(R.string.sound), fontSize = 12.sp, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GameActionButton(size = system_button_size, color = Color(0xFF2dc421), onAction = {
                    gamer.pauseOrResume()
                })
                Spacer(Modifier.height(spacer_height))
                Text(stringResource(R.string.pause_and_resume), fontSize = 12.sp, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GameActionButton(size = system_button_size, color = Color(0xFFF44336), onAction = {
                    gamer.reset()
                })
                Spacer(Modifier.height(spacer_height))
                Text(stringResource(R.string.reset), fontSize = 12.sp, color = Color.Black)
            }
        }
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            GameActionButton(size = 90.dp, onAction = {
                gamer.drop()
            })
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameActionButton(
    modifier: Modifier = Modifier,
    size: Dp = direction_button_size,
    color: Color = Color(0xFF2196F3),
    onAction: () -> Unit = {}
) {


    var isDown by remember {
        mutableStateOf(false)
    }

    val alpha: Float by animateFloatAsState(if (!isDown) 1f else 0.5f)

    val scope = rememberCoroutineScope()
    var longPressJob: Job? by remember {
        mutableStateOf(null)
    }
    val context = LocalContext.current
    Canvas(modifier = modifier
        .size(size = size)
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    logx("action down")
                    onAction()
                    isDown = true
                    scope.cancel()
                    longPressJob = scope.launch {
                        delay(300)
                        launch {
                            while (isActive) {
                                delay(60)
                                logx("do action while is press")
                                onAction()
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    logx("action up")
                    logx("current longPressJob? ${longPressJob}")
                    longPressJob?.cancel()
                    isDown = false
                }
                // not found trigger this event
                MotionEvent.ACTION_CANCEL -> {
                    logx { "action cancel" }
                    longPressJob?.cancel()
                    isDown = false
                }
            }
            true
        }
    ) {
        val shadowWidth = button_shadow_stroke_width.toPx()
        // refer to https://stackoverflow.com/questions/5197892/add-shadow-to-custom-shape-on-android
        drawImage(
            context.resources.getDrawable(R.drawable.game_btn_shadow, null).toBitmap(
                width = size.toPx().toInt(),
                height = size.toPx().toInt(),
            ).asImageBitmap()
        )


        // draw shadow
//        drawCircle(
//            color = Color(0x20000000),
//            style = Stroke(width = shadowWidth*2)
//        )
//         draw content
//        inset(shadowWidth / 2) {
//        }
        drawCircle(color = color.copy(alpha = alpha), size.toPx() / 2 - shadowWidth / 2)

    }


}

@Preview
@Composable
fun PreviewGameController() {
//    GameController()
}

@Preview
@Composable
fun PreViewLeftGameController() {
//    LeftController()
}