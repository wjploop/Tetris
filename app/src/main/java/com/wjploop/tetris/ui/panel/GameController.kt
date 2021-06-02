package com.wjploop.tetris.ui.panel

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wjploop.tetris.ui.gamer.Gamer
import com.wjploop.tetris.ui.gamer.LocalGamer
import com.wjploop.tetris.ui.material.LocalSound
import com.wjploop.tetris.ui.material.Sound

private val direction_button_size = 48.dp
private val system_button_size = 28.dp
private val spacer_height = 8.dp

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
        // todo 支持长按发送命令
        // todo 按钮实现拟物效果
        GameActionButton(Modifier.align(Alignment.TopCenter), onClick = { gamer.rotate() })
        GameActionButton(Modifier.align(Alignment.CenterEnd), onClick = { gamer.right() })
        GameActionButton(Modifier.align(Alignment.BottomCenter), onClick = { gamer.down(true) })
        GameActionButton(Modifier.align(Alignment.CenterStart), onClick = { gamer.left() })

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
                GameActionButton(size = system_button_size, color = Color(0xFF2dc421), onClick = {
                    gamer.sounds()
                })
                Spacer(Modifier.height(spacer_height))
                Text("声音", fontSize = 12.sp, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GameActionButton(size = system_button_size, color = Color(0xFF2dc421), onClick = {
                    gamer.pauseOrResume()
                })
                Spacer(Modifier.height(spacer_height))
                Text("暂停/恢复", fontSize = 12.sp, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                GameActionButton(size = system_button_size, color = Color(0xFFF44336), onClick = {
                    gamer.reset()
                })
                Spacer(Modifier.height(spacer_height))
                Text("重置", fontSize = 12.sp, color = Color.Black)
            }
        }
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            GameActionButton(size = 90.dp, onClick = {
                gamer.drop()
            })
        }
    }
}

@Composable
fun GameActionButton(
    modifier: Modifier = Modifier,
    size: Dp = direction_button_size,
    color: Color = Color(0xFF2196F3),
    onClick: () -> Unit = {}
) {

    Button(
        onClick = onClick,
        colors = buttonColors(color),
        shape = CircleShape,
        modifier = modifier
            .size(size = size)
    ) {

    }
}

@Preview
@Composable
fun PreviewGameController() {
    GameController()
}

@Preview
@Composable
fun PreViewLeftGameController() {
    LeftController()
}