package com.wjploop.tetris.ui.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wjploop.tetris.ui.gamer.*
import com.wjploop.tetris.ui.material.Brick
import com.wjploop.tetris.ui.material.BrickSize
import com.wjploop.tetris.ui.material.BrickType
import com.wjploop.tetris.ui.material.LocalBrickSize
import java.util.*
import kotlin.concurrent.timerTask

val SCREEN_BACKGROUND = Color(0xff9ead86)
val playPadPadding = 3.dp

fun getBrickSizeFor(playPanelWidth: Dp) = Size(
    width = (playPanelWidth - playPadPadding * 2).value / GAME_PAD_MATRIX_W,
    height = (playPanelWidth - playPadPadding * 2).value / GAME_PAD_MATRIX_W
)

@Composable
fun Screen(width: Dp) {
    val playPanelWidth = width * 0.6f
    val playPanelHeight =
        getBrickSizeFor(playPanelWidth).width.dp * GAME_PAD_MATRIX_H + playPadPadding * 2f

    BoxWithConstraints(
        Modifier
            .size(width = width, playPanelHeight)
            .background(color = SCREEN_BACKGROUND)
    ) {
        CompositionLocalProvider(
            LocalBrickSize provides BrickSize(
                size = getBrickSizeFor(playPanelWidth)
            )
        ) {
            Row {
                PlayerPanel(playPanelWidth)
                StatusPanel()
            }
        }
    }
}

@Composable
fun PlayerPanel(width: Dp) {
    BoxWithConstraints(
        Modifier
            .size(width = width, height = width * 2)
            .padding(2.dp)
            .border(2.dp, Color.Black)
    ) {
        PlayerPad()
        GameUninitialized()
    }
}

@Composable
fun GameUninitialized(gameData: GameData = LocalGameData.current) {
    if (gameData.gameState == GameState.none) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconDragon(animate = true)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Tetris", fontSize = 20.sp)
            }
        }
    }
}


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun StatusPanel(gameData: GameData = LocalGameData.current) {
    Column(Modifier.padding(8.dp)) {
        Text("分数", style = TextStyle(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(4.dp))
        GamerNumber(gameData.points)
        Spacer(modifier = Modifier.height(10.dp))

        Text("消除", style = TextStyle(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(4.dp))
        GamerNumber(gameData.clear)
        Spacer(modifier = Modifier.height(10.dp))

        Text("级别", style = TextStyle(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(4.dp))
        GamerNumber(gameData.level)
        Spacer(modifier = Modifier.height(10.dp))

        Text("下一个", style = TextStyle(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(4.dp))
        NextBlock()

        Spacer(modifier = Modifier.weight(1f))

        Text("status : ${gameData.gameState}")
        Spacer(modifier = Modifier.weight(0.5f))

        GameStatus()

    }
}

@ExperimentalStdlibApi
@Composable
fun GameStatus(gameData: GameData = LocalGameData.current) {

    val timer by remember {
        mutableStateOf(Timer())
    }
    val (hour, setHour) = remember {
        mutableStateOf(0)
    }

    val (minute, setMinute) = remember {
        mutableStateOf(0)
    }

    var colon by remember {
        mutableStateOf(false)
    }

    DisposableEffect(key1 = timer) {
        timer.schedule(timerTask {
            val calendar = Calendar.getInstance()
            setHour(calendar.get(Calendar.HOUR_OF_DAY))
            setMinute(calendar.get(Calendar.MINUTE))
            colon = !colon
        }, 0, 1000)
        onDispose {
            timer.cancel()
        }
    }


    Row {
        IconSound(enable = gameData.mute)
        Spacer(modifier = Modifier.width(4.dp))
        IconPause(enable = gameData.gameState == GameState.paused)
        Spacer(modifier = Modifier.weight(weight = 1.0F, fill = true))
        GamerNumber(number = hour, 2, true)
        IconColon(enable = colon)
        GamerNumber(number = minute, 2, true)
    }
}


@Composable
fun NextBlock(gameData: GameData = LocalGameData.current) {
    val data = Array(2) { IntArray(4) }
    gameData.next?.shape?.forEachIndexed { i, row ->
        row.forEachIndexed { j, value ->
            data[i][j] = value
        }
    }
    Column {
        data.map {
            Row {
                it.map {
                    Brick(if (it == 1) BrickType.normal else BrickType.empty)
                }
            }
        }
    }
}


@Composable
fun PlayerPad(
    gameData: GameData = LocalGameData.current,
    gamer: Gamer = LocalGamer.current
) {

    val data = gameData.data

    Column {

        data.mapIndexed { i, cols ->
            Row {
                cols.mapIndexed { j, row ->
                    Brick(
                        brickType = when (data[i][j]) {
                            0 -> BrickType.empty
                            1 -> BrickType.normal
                            2 -> BrickType.hilight
                            -1 -> BrickType.empty
                            else -> throw Exception("no such brickType: $row")
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun PreViewScreen() {
    Screen(width = 360.dp)
}