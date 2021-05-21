package com.wjploop.tetris.ui.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wjploop.tetris.ui.gamer.*
import com.wjploop.tetris.ui.material.Brick
import com.wjploop.tetris.ui.material.BrickSize
import com.wjploop.tetris.ui.material.BrickType
import com.wjploop.tetris.ui.material.LocalBrickSize

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


//    val playPanelHeight = playPanelWidth * 2

    BoxWithConstraints(
        Modifier
            .size(width = width, playPanelHeight)
            .background(color = SCREEN_BACKGROUND)
    ) {
        Row {
            CompositionLocalProvider(
                LocalBrickSize provides BrickSize(
                    size = getBrickSizeFor(playPanelWidth)
                )
            ) {
                PlayerPad(playPanelWidth)
            }
        }
    }
}

@Composable
fun PlayerPad(
    playPanelWidth: Dp, gameData: GameData = LocalGameData.current,
    gamer: Gamer = LocalGamer.current
) {
    BoxWithConstraints(
        Modifier
            .size(width = playPanelWidth, (playPanelWidth * 2))
            .border(2.dp, Color.Black)
            .padding(playPadPadding)
    ) {
        fun countData(count: Int): List<List<Int>> =
            (0..GAME_PAD_MATRIX_H).map { i ->
                IntArray(GAME_PAD_MATRIX_W) { j ->
                    if (count > i * GAME_PAD_MATRIX_W + j) {
                        0
                    } else {
                        -1
                    }
                }.toMutableList()
            }.toMutableList()


        var count by remember {
            mutableStateOf(8)
        }

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

        val scope = rememberCoroutineScope()
        val composable = currentRecomposeScope
        Button(
            modifier = Modifier.align(Alignment.BottomEnd),

            onClick = {
//                scope.launch {
////                    count++
//                    repeat(100){
////                        gameData.data = countData(it)
//                        composable.invalidate()
//                        delay(1000)
//                    }
//                }
                gamer.startGame()
            }) {
            Text("Level ${LocalGameData.current.level}")
        }
    }
}


@Preview
@Composable
fun PreViewScreen() {
    Screen(width = 360.dp)
}