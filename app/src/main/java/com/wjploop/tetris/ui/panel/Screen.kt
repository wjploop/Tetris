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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
                PlayerPad(playPanelWidth)
                StatusPanel()
            }
        }
    }
}

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

        Text("status : ${gameData.gameState}")
    }
}

@Composable
fun PlayerPad(
    playPanelWidth: Dp,
    gameData: GameData = LocalGameData.current,
    gamer: Gamer = LocalGamer.current
) {
    BoxWithConstraints(
        Modifier
            .size(width = playPanelWidth, (playPanelWidth * 2))
            .border(2.dp, Color.Black)
            .padding(playPadPadding)
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
}


@Preview
@Composable
fun PreViewScreen() {
    Screen(width = 360.dp)
}