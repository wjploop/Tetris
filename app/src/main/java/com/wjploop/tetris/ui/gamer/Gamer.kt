package com.wjploop.tetris.ui.gamer

import androidx.compose.runtime.*

///the height of game pad
const val GAME_PAD_MATRIX_H = 20

///the width of game pad
const val GAME_PAD_MATRIX_W = 10

enum class GameState {
    ///随时可以开启一把惊险而又刺激的俄罗斯方块
    none,

    ///游戏暂停中，方块的下落将会停止
    paused,

    ///游戏正在进行中，方块正在下落
    ///按键可交互
    running,

    ///游戏正在重置
    ///重置完成之后，[GameController]状态将会迁移为[none]
    reset,

    ///下落方块已经到达底部，此时正在将方块固定在游戏矩阵中
    ///固定完成之后，将会立即开始下一个方块的下落任务
    mixing,

    ///正在消除行
    ///消除完成之后，将会立刻开始下一个方块的下落任务
    clear,

    ///方块快速下坠到底部
    drop,
}

data class GameData(
    val gameState: GameState = GameState.none,

    var data: List<List<Int>> = listOf(),
    val mask: List<List<Int>> = listOf(),


    var level: Int = 1,
    val points: Int = 0,
    val clear: Int = 0,
    var count: MutableState<Int> = mutableStateOf(0),
) {
    init {
        data = Array(GAME_PAD_MATRIX_H) {
            IntArray(GAME_PAD_MATRIX_W) { -1 }.toList()
        }.toList()
    }
}

val LocalGameControl = compositionLocalOf<GameData> {
    error("no find game control on this tree")
}

@Composable
fun Game(
    child: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalGameControl provides GameData()
    ) {
        child()
    }
}


