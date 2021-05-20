package com.wjploop.tetris.ui.gamer

import android.opengl.GLSurfaceView
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

    val data: Array<IntArray> = emptyArray(),

    val level: Int = 1,
    val points: Int = 0,
    val clear: Int = 0,

    val next: Block,
    val onNewGameSate: (GameState) -> Unit,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameData

        if (gameState != other.gameState) return false
        if (!data.contentDeepEquals(other.data)) return false
        if (level != other.level) return false
        if (points != other.points) return false
        if (clear != other.clear) return false
        if (next != other.next) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gameState.hashCode()
        result = 31 * result + data.contentDeepHashCode()
        result = 31 * result + level
        result = 31 * result + points
        result = 31 * result + clear
        result = 31 * result + next.hashCode()
        return result
    }
}

val LocalGameControl = compositionLocalOf<GameData> {
    error("no find game control on this tree")
}

@Composable
fun Game(

    child: @Composable () -> Unit
) {

    val data: Array<IntArray> = emptyArray()
    val mixed: Array<IntArray> = emptyArray()



    var current: Block
    val next = Block.random()

    fun onNewGameState(state: GameState) {

    }
    var (gameData, setGameData) = remember {
        mutableStateOf(
            GameData(onNewGameSate = {
                onNewGameState(it)
            }, next = next)
        )
    }


//    fun computeNextData(): Array<IntArray> {
//
//        val newData = IntArray(GAME_PAD_MATRIX_H) { y ->
//            for (x in 0 until GAME_PAD_MATRIX_W) {
//                val row = IntArray(GAME_PAD_MATRIX_W)
//                if (current.occupy(x, y)) {
//
//                } else {
//                    row[x] = gameData.data[]
//                }
//            }
//        }
//    }


    CompositionLocalProvider(
        LocalGameControl provides gameData
    ) {
        child()
    }
}


