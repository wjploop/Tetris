package com.wjploop.tetris.ui.gamer

enum class BlockType(
    val shape: Array<IntArray>,
    val startXY: IntArray,
    val origin: Array<IntArray>
) {
    I(
        shape = arrayOf(intArrayOf(1, 1, 1, 1)),
        startXY = intArrayOf(3, 0),
        origin = arrayOf(
            intArrayOf(1, -1),
            intArrayOf(-1, 1)
        )
    ),
    L(
        shape = arrayOf(
            intArrayOf(0, 0, 1),
            intArrayOf(1, 1, 1)
        ),
        startXY = intArrayOf(4, -1),
        origin = arrayOf(
            intArrayOf(0, 0)
        )
    ),
    J(
        shape = arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(1, 1, 1)
        ),
        startXY = intArrayOf(4, -1),
        origin = arrayOf(
            intArrayOf(0, 0)
        )
    ),
    Z(
        shape = arrayOf(
            intArrayOf(1, 1, 0),
            intArrayOf(0, 1, 1)
        ),
        startXY = intArrayOf(4, -1),
        origin = arrayOf(intArrayOf(0, 0))
    ),
    S(
        shape = arrayOf(
            intArrayOf(0, 1, 1),
            intArrayOf(1, 1, 0)
        ),
        startXY = intArrayOf(4, -1),
        origin = arrayOf(intArrayOf(0, 0))
    ),
    O(
        shape = arrayOf(
            intArrayOf(1, 1),
            intArrayOf(1, 1)
        ),
        startXY = intArrayOf(4, -1),
        origin = arrayOf(intArrayOf(0, 0))
    ),
    T(
        shape = arrayOf(
            intArrayOf(0, 1, 0),
            intArrayOf(1, 1, 1)
        ),
        startXY = intArrayOf(4, -1),
        origin = arrayOf()
    )
}


data class Block(
    val type: BlockType,
    val shape: Array<IntArray>,
    val xy: IntArray,
    val rotateIndex: Int
) {
    fun fall(step: Int = 1) = copy(xy = intArrayOf(xy[0], xy[1] + step))
    fun left() = copy(xy = intArrayOf(xy[0] - 1, xy[1]))
    fun right() = copy(xy = intArrayOf(xy[0] + 1, xy[1]))

    fun isValidInMatrix(matrix: Array<IntArray>): Boolean {
        // 砖块是否出界
        val blockOut = xy[0] < 0 ||
                xy[0] + shape[0].size > GAME_PAD_MATRIX_W ||
                xy[1] + shape.size > GAME_PAD_MATRIX_H
        if (blockOut) {
            return false
        }
        // 砖块占据的位置，是否与矩阵中砖块冲突
        matrix.forEachIndexed { y, rows ->
            rows.forEachIndexed { x, value ->
                if (value == 1 && occupy(y, x)) {
                    return false
                }
            }
        }
        return true
    }

    // 该砖块是否占据坐标 [y,x]
    fun occupy(x: Int, y: Int): Boolean {
        // 先移动原点
        val xx = x - xy[0]
        val yy = x - xy[1]
        if (xx < 0 || xx >= shape[0].size || yy < 0 || yy > shape.size) {
            return false;
        }
        return shape[yy][xx] == 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (type != other.type) return false
        if (!xy.contentEquals(other.xy)) return false
        if (rotateIndex != other.rotateIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + xy.contentHashCode()
        result = 31 * result + rotateIndex.hashCode()
        return result
    }

    companion object {
        fun fromType(type: BlockType) = Block(type, shape = type.shape, xy = type.startXY, 0)

        fun random() = fromType(BlockType.values().random())
    }
}