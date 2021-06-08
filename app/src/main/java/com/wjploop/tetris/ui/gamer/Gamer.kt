package com.wjploop.tetris.ui.gamer

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import com.wjploop.tetris.ext.logx
import com.wjploop.tetris.ui.material.LocalMaterial
import com.wjploop.tetris.ui.material.LocalSound
import com.wjploop.tetris.ui.material.MaterialDataWrapper
import com.wjploop.tetris.ui.material.Sound
import kotlinx.coroutines.*
import java.util.*
import com.wjploop.tetris.R

///the height of game pad
const val GAME_PAD_MATRIX_H = 20

///the width of game pad
const val GAME_PAD_MATRIX_W = 10

const val GAME_LEVEL_MIN = 1
const val GAME_LEVEL_MAX = 6

val SPEED = intArrayOf(
    800, 650, 500, 370, 250, 160
)

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
    val mute: Boolean = false,

    val next: Block? = null,
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
        if (mute != other.mute) return false

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
    Gamer(sound = Sound(ContextWrapper(null)), MainScope()) {}
}

// 用于处理游戏状态迁移
// 接收来孩子节点的Event，更新后，更新时返回一个新的GameData
class Gamer(
    val sound: Sound,
    val gameScope: CoroutineScope, val setGameData: (GameData) -> Unit,
) {

    private val data: Array<IntArray> = Array(GAME_PAD_MATRIX_H) {
        IntArray(GAME_PAD_MATRIX_W)
    }
    private val mask: Array<IntArray> = Array(GAME_PAD_MATRIX_H) {
        IntArray(GAME_PAD_MATRIX_W)
    }

    private var _level = GAME_LEVEL_MIN

    private var _points = 0

    private var _cleared = 0

    private var _state = GameState.none

    // todo 当前使用 current 是否为空隐式表达了一个游戏状态，换个方式显式表达出来更好
    private var _current: Block? = null

    private var _next = Block.random()


    fun pauseOrResume() {
        if (_state == GameState.running) {
            pause()
        } else if (_state in arrayOf(GameState.none, GameState.paused)) {
            startGame()
        }
    }

    private fun startGame() {
        if (_state == GameState.running && fallJob?.isActive == false) {
            return
        }
        onNewGameState(GameState.running)
        autoFall(true)
    }


    // 检验当前是游戏运行状态
    private fun performGameAction(action: (current: Block) -> Unit) {
        val actionName = action.javaClass.name.split("$").getOrNull(1)
        if (_state != GameState.running) {
//            logx("can't do action $actionName because current state is not ${GameState.running} but $_state ")
            return
        }
        _current?.let {
            action(it)
        } ?: logx("can't do action $actionName because current is null")
    }


    // todo 几个游戏动作公共部分提取出来
    // 区分 自动下落 和 用户手动下落， 后者播放音效
    fun down(enableSound: Boolean = false) {
        performGameAction {
            gameScope.launch {
                val next = it.down()
                if (next.isNotConflict(data)) {
                    _current = next
                    onNewGameState()
                    if (enableSound) {
                        sound.move()
                    }
                } else {
                    mixCurrentInToData()
                }
            }
        }
    }

    fun left() {
        if (_state == GameState.none) {
            if (_level > GAME_LEVEL_MIN) {
                _level--
            }
        } else {
            performGameAction {
                val next = it.left()
                if (next.isNotConflict(data)) {
                    _current = next
                    sound.move()
                }
            }
        }
        onNewGameState()
    }

    fun right() {
        if (_state == GameState.none) {
            if (_level < GAME_LEVEL_MAX) {
                _level++
            }
        } else {
            performGameAction {
                val next = it.right()
                if (next.isNotConflict(data)) {
                    _current = next
                    sound.move()
                }
            }
        }
        onNewGameState()
    }

    fun rotate() {
        performGameAction {
            val next = it.rotate()
            if (next.isNotConflict(data)) {
                _current = next
                sound.rotate()
                onNewGameState()
            }
        }
    }

    fun drop() {
        if (_state == GameState.running) {
            performGameAction {
                var cur = it
                var next = cur.down()
                while (next.isNotConflict(data)) {
                    cur = next
                    next = next.down()
                }
                _current = cur
                gameScope.launch {
                    onNewGameState(GameState.drop)
                    // 缓一会，依赖这个drop状态执行动画
                    delay(200)
                    mixCurrentInToData()
                }
            }
        } else if (_state in arrayOf(GameState.none)) {
            startGame()
        }
    }

    fun sounds() {
        sound.mute = !sound.mute
        onNewGameState()
    }

    fun pause() {
        onNewGameState(GameState.paused)
    }


    fun reset() {

        gameScope.launch {
            if (_state == GameState.none) {
                startGame()
                return@launch
            }
            if (_state == GameState.reset) {
                return@launch
            }
            sound.start()
            onNewGameState(GameState.reset)

            // 执行一段动画，从下到上铺满格子，再从上到下清除格子
            for (i in GAME_PAD_MATRIX_H - 1 downTo 0) {
                data[i].fill(1)
                onNewGameState(GameState.reset)
                delay(50)
            }
            _current = null
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
        return _next.also {
            _next = Block.random()
        }
    }

    fun onNewGameState(state: GameState = this._state) {
        this._state = state
        gameScope.launch {
            setGameData(
                GameData(
                    gameState = state,
                    data = computeData(),
                    level = _level,
                    points = _points,
                    clear = _cleared,
                    next = _next,
                    mute = sound.mute,
                )
            )
        }
    }


    private fun computeData(): Array<IntArray> {
        val newData = Array(GAME_PAD_MATRIX_H) { y ->
            val row = IntArray(GAME_PAD_MATRIX_W)
            for (x in 0 until GAME_PAD_MATRIX_W) {
                var value = if (_current?.occupy(y, x) == true) {
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


    private suspend fun mixCurrentInToData() {
        autoFall(false)

        // 更新data，将current混入原来的data
        performActionOnPad { i, j ->
            data[i][j] = if (_current?.occupy(i, j) == true) 1 else data[i][j]
            if (i == 0 && data[0].contains(1)) {
                logx("no, error in update data")
            }
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
            sound.clear()
            clearLines.forEach {
                mask[it].fill(1)
                onNewGameState()
                delay(100)
            }
            clearLines.forEach {
                mask[it].fill(0)
            }

            // 移除消除的行
            val newData =
                data.toMutableList().filterIndexed { index, _ -> !clearLines.contains(index) }
                    .toMutableList()
                    .apply {
                        repeat(clearLines.size) {
                            add(0, IntArray(GAME_PAD_MATRIX_W))
                        }
                    }
                    .toList()
                    .toTypedArray()
            newData.copyInto(data)

            _cleared += clearLines.size
            _points += clearLines.size * _level * 5

            // todo 消除50行上升一个等级，这算法似乎不够好，
            _level = (_cleared / 50).coerceIn(_level, GAME_LEVEL_MAX)

        } else {
            Log.d("wolf", "mixed no clear")
            onNewGameState(GameState.mixing)
            sound.drop()
            performActionOnPad { i, j ->
                // 高亮落到底部的砖块
                mask[i][j] = if (_current?.occupy(i, j) == true) {
                    1
                } else {
                    mask[i][j]
                }
                false
            }
            // 高亮的砖块在200毫秒后去掉高亮
            delay(200)
            performActionOnPad { i, j ->
                mask[i][j] = 0
                false
            }

        }

        // current 已经融入data
        _current = null

        // 砖块触顶，结束游戏
        if (data[0].contains(1)) {
            logx("reset and current matrix ${formatMatrix(data)}")
            reset()
        } else {
            startGame()
        }
    }


    var fallJob: Job? = null

    private fun autoFall(enable: Boolean) {
        if (!enable && fallJob != null) {
            fallJob?.cancel()
            fallJob = null
        } else if (enable) {
            fallJob?.cancel()
            _current = _current ?: getNextBlock()
            fallJob = gameScope.launch {
                while (true) {
                    down(false)
                    delay(SPEED[_level - 1].toLong())
                }
            }
        }

    }
}

private fun formatMatrix(matrix: Array<IntArray>): String {
    return "matrix:\n" + matrix.contentDeepToString().drop(1).dropLast(1).replace("], ", "]\n")
}

@SuppressLint("ResourceType")
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

    val sound = LocalSound.current
    val gameScope = rememberCoroutineScope()

    val gamer = remember {
        Gamer(sound = sound, gameScope = gameScope, setGameData = setGameData)
    }

    val context = LocalContext.current
    // worked, but android recommend put in the raw directory
//    val bitmap = BitmapFactory.decodeStream(context.assets.open("raw/material.png"),)
    // not worked, not expected bitmap
    // 为什么呢？
//    val bitmap = BitmapFactory.decodeResource(context.resources, R.raw.material, null)

    // 注意放在drawable目录的图片，aapt去掉一些不必要的信息

    val bitmap = BitmapFactory.decodeStream(context.resources.openRawResource(R.raw.material))


//    val imageBitmap = ImageBitmap.imageResource(id = R.raw.material)
    val imageBitmap = bitmap.asImageBitmap()

    val materialDataWrapper = remember(imageBitmap) {
        MaterialDataWrapper(bitmap = imageBitmap)
    }

    CompositionLocalProvider(
        LocalGameData provides gameData,
        LocalGamer provides gamer,
        LocalMaterial provides materialDataWrapper,
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