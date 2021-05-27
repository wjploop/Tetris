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
        origin = arrayOf(
            intArrayOf(0, 0),
            intArrayOf(0, 1),
            intArrayOf(1, -1),
            intArrayOf(-1, 0),
        )
    );

    override fun toString(): String {
        return "name=$name)"
    }
}


data class Block(
    val type: BlockType,
    val shape: Array<IntArray>,
    val topLeft: IntArray,
    val rotateIndex: Int
) {
    val height = shape.size
    val width = shape[0].size
    val left = topLeft[0]
    val top = topLeft[1]

    fun down(step: Int = 1) = copy(topLeft = intArrayOf(left, top + step))
    fun left() = copy(topLeft = intArrayOf(left - 1, top))
    fun right() = copy(topLeft = intArrayOf(left + 1, top))
    fun rotate(): Block {

        // 顺时针翻转一个矩形
        // 123
        // 456
        // =>
        // 41
        // 52
        // 63
        val nextShape = Array<IntArray>(shape[0].size) { IntArray(shape.size) }
        for (i in nextShape.indices) {
            for (j in nextShape[0].indices) {
                // 3 = [2,0] = [0,2] == [2-1 - 1][2]
                nextShape[i][j] = shape[shape.size - 1 - j][i]
            }
        }
        val nextTopLeft = intArrayOf(
            topLeft[0] + type.origin[rotateIndex][0],
            topLeft[1] + type.origin[rotateIndex][1]
        )
        val nextRotateIndex = if (rotateIndex + 1 >= type.origin.size) 0 else rotateIndex + 1
        return Block(type, nextShape, nextTopLeft, nextRotateIndex)
    }

    fun isNotConflict(matrix: Array<IntArray>): Boolean {
        // 砖块是否出界
        val blockOut = left < 0 ||
                left + width > GAME_PAD_MATRIX_W ||
                top + height > GAME_PAD_MATRIX_H
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

    // 该砖块是否占据坐标 [y,x] ?
    fun occupy(y: Int, x: Int): Boolean {
        // 先移动原点，
        val xx = x - left
        val yy = y - top
        //
        if (xx < 0 || xx >= width || yy < 0 || yy >= height) {
            return false;
        }
        return shape[yy][xx] == 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (type != other.type) return false
        if (!topLeft.contentEquals(other.topLeft)) return false
        if (rotateIndex != other.rotateIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + topLeft.contentHashCode()
        result = 31 * result + rotateIndex.hashCode()
        return result
    }

    companion object {
        fun fromType(type: BlockType) = Block(type, shape = type.shape, topLeft = type.startXY, 0)

        fun random() = fromType(BlockType.values().random())
    }
}