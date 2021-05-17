package com.wjploop.tetris.ui.gamer

import androidx.compose.runtime.Immutable

///the height of game pad
val GAME_PAD_MATRIX_H = 20;

///the width of game pad
val GAME_PAD_MATRIX_W = 10;

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

@Immutable
data class GameModel(
    val data: List<List<Int>> = listOf(),
    val mask: List<List<Int>> = listOf(),

    val level: Int = 1,
    val points: Int = 0,
    val clear: Int = 0,

    )


