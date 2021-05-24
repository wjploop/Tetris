package com.wjploop.tetris.ui.gamer

import android.util.Log
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

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

    val next: Block? = null,
    val onNewGameSate: (GameState) -> Unit = {},
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

val LocalGameData = compositionLocalOf<GameData> {
    // AS 没法处理好这个
//    error("no find game data")
    GameData()
}


val LocalGamer = compositionLocalOf<Gamer> {
//    error("no find gamer")
    Gamer(MainScope()) {}
}

// 用于处理游戏状态迁移
// 接收来孩子节点的Event，更新后，更新时返回一个新的GameData
class Gamer(val gameScope: CoroutineScope, val setGameData: (GameData) -> Unit) {

    private val data: Array<IntArray> = Array(GAME_PAD_MATRIX_H) {
        IntArray(GAME_PAD_MATRIX_W)
    }
    private val mask: Array<IntArray> = Array(GAME_PAD_MATRIX_H) {
        IntArray(GAME_PAD_MATRIX_W)
    }

    var level = 0

    var state = GameState.none

    var current: Block? = null

    var next = Block.random()

    fun pauseOrResume() {
        if (state == GameState.running) {
            pause()
        } else if (state in arrayOf(GameState.none, GameState.paused)) {
            startGame()
        }
    }

    fun startGame() {
        autoFall()
    }


    fun down() {
        current?.fall()?.let {
            if (it.isNotConflict(data)) {
                current = it
                onNewGameState(GameState.running)
            } else {
                mixCurrentInToData()
            }
        }
    }

    fun left() {
        if (current?.left()?.isNotConflict(data) == true) {
            current = current?.left()
            onNewGameState(GameState.running)
        }
    }

    fun right() {
        if (current?.right()?.isNotConflict(data) == true) {
            current = current?.right()
            onNewGameState(GameState.running)
        }
    }

    fun rotate() {
        if (current?.rotate()?.isNotConflict(data) == true) {
            current = current?.rotate()
            onNewGameState(GameState.running)
        }
    }

    fun drop() {
        if (state == GameState.running && current != null) {
            var next = current ?: return
            while (next.fall().isNotConflict(data)) {
                next = next.fall()
            }
            current = next
            gameScope.launch {
                delay(100)
                onNewGameState(GameState.drop)
                mixCurrentInToData()
            }
        } else if (state in arrayOf(GameState.none, GameState.paused)) {
            startGame()
        }
    }

    fun sounds() {

    }

    fun pause() {

    }


    fun reset() {
        // 执行一段动画，从下到上铺满格子，再从上到下清除格子
        gameScope.launch {
            for (i in GAME_PAD_MATRIX_H - 1 downTo 0) {
                data[i].fill(1)
                onNewGameState(GameState.reset)
                delay(50)
            }
            current = null
            getNextBlock()
            for (i in 0 until GAME_PAD_MATRIX_H) {
                data[i].fill(0)
                onNewGameState(GameState.reset)
                delay(50)
            }
            onNewGameState(GameState.none)
        }

    }


    private fun getNextBlock(): Block {
        return Block.random().also {
            next = it
        }
    }

    fun onNewGameState(state: GameState) {
        this.state = state
        gameScope.launch {
//            delay(1000)
//            level++
            Log.d("wolf", "level $level")

            Log.d("wolf", formatMatrix(data))
            setGameData(
                GameData(
                    gameState = state,
                    data = computeData(),
                    level = level,
                    0,
                    0,
                    next = next,
                    onNewGameSate = {
                        onNewGameState(it)
                    })
            )
        }
    }


    private fun computeData(): Array<IntArray> {
        val newData = Array(GAME_PAD_MATRIX_H) { y ->
            val row = IntArray(GAME_PAD_MATRIX_W)
            for (x in 0 until GAME_PAD_MATRIX_W) {
                var value = if (current?.occupy(y, x) == true) {
                    1
                } else {
                    data[y][x]
                }
                // 注意处理 mask效果，叠加到data之上得到最终效果
                if (mask[y][x] == -1) {
                    value = 0
                } else if (mask[y][x] == 1) {
                    value = 2
                }
                row[x] = value
            }
            row
        }
        return newData
    }


    private fun mixCurrentInToData() {
        fallJob?.cancel()

        // 更新data，将current混入原来的data
        performActionOnPad { i, j ->
            data[i][j] = if (current?.occupy(i, j) == true) 1 else data[i][j]
            false
        }

        // 检查data是否有可以清楚行
        val clearLines = mutableListOf<Int>()
        data.forEachIndexed { index, row ->
            if (row.all { it == 1 }) {
                clearLines.add(index)
            }
        }

        if (clearLines.isNotEmpty()) {
            Log.d("wolf", "mixed and clear lines $clearLines")
        } else {
            Log.d("wolf", "mixed no clear")
            onNewGameState(GameState.mixing)
            performActionOnPad { i, j ->
                // 高亮落到底部的砖块
                mask[i][j] = if (current?.occupy(i, j) == true) {
                    1
                } else {
                    mask[i][j]
                }
                false
            }
            gameScope.launch {
                // 高亮的砖块在200毫秒后去掉高亮
                delay(200)
                performActionOnPad { i, j ->
                    mask[i][j] = 0
                    false
                }
            }
        }

        // current 已经融入data
        current = null

        // 砖块触顶，结束游戏
        if (data[0].contains(1)) {
            reset()
        } else {
            startGame()
        }
    }


    var fallJob: Job? = null

    private fun autoFall() {
        current = current ?: getNextBlock()
        fallJob?.cancel()
        fallJob = gameScope.launch {
            while (true) {
                down()
                delay(1000)
            }
        }
    }
}

private fun formatMatrix(matrix: Array<IntArray>): String {
    return "matrix:\n" + matrix.contentDeepToString().drop(1).dropLast(1).replace("], ", "]\n")
}

@Composable
fun Game(
    child: @Composable () -> Unit
) {
    val (gameData, setGameData) = remember {
        mutableStateOf(
            GameData(data = Array(GAME_PAD_MATRIX_H) {
                IntArray(GAME_PAD_MATRIX_W)
            })
        )
    }
    val gameScope = rememberCoroutineScope()

    val gamer = remember {
        Gamer(gameScope = gameScope, setGameData = setGameData)
    }

    CompositionLocalProvider(
        LocalGameData provides gameData,
        LocalGamer provides gamer
    ) {
        child()
    }
}


fun performActionOnPad(action: (i: Int, j: Int) -> Boolean) {

    for (i in 0 until GAME_PAD_MATRIX_H) {
        for (j in 0 until GAME_PAD_MATRIX_W) {
            val shouldBreak = action(i, j)
            if (shouldBreak) {
                break
            }
        }
    }
}