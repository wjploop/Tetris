package com.wjploop.tetris.ext

import android.util.Log
import kotlin.math.log

fun logx(str: String, tag: String = "wolf") {
    Log.d(tag, str)
}

fun logx(tag: String = "wolf", string: () -> String) {
    Log.d(tag, string())
}
