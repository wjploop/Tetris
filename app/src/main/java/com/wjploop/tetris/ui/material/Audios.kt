package com.wjploop.tetris.ui.material

import android.content.Context
import android.content.ContextWrapper
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.wjploop.tetris.R
import com.wjploop.tetris.ext.logx

val LocalSound = compositionLocalOf {
    Sound(ContextWrapper(null))
}

class Sound(context: Context) {

    private val sound: SoundPool = SoundPool.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .setMaxStreams(3)
        .build()

    private val soundResIds = intArrayOf(
        R.raw.clean,
        R.raw.drop,
        R.raw.explosion,
        R.raw.move,
        R.raw.rotate,
        R.raw.start,
    )

    // todo 应该可以懒加载这些资源,或开个线程加载
    // load resId to sound id, so that the sound pool can play it
    private val soundIds = soundResIds.map { resId ->
        Pair(resId, sound.load(context, resId, 1))
    }.toMap()

    private fun play(resId: Int) {
        sound.play(
            soundIds[resId] ?: error("the sound resId $resId hadn't loaded"),
            1f,
            1f,
            1,
            0,
            1f
        ).let {
            logx("play result $it")
        }
    }

    fun start() {
        play(R.raw.start)
    }

    fun clear() {
        play(R.raw.clean)
    }

    fun drop() {
        play(R.raw.drop)
    }

    fun rotate() {
        play(R.raw.rotate)
    }

    fun move() {
        play(R.raw.move)
    }
}

@Composable
fun SoundWidget(content: @Composable () -> Unit) {

    val context = LocalContext.current
    val sound = remember {
        Sound(context = context)
    }

    CompositionLocalProvider(
        LocalSound provides sound
    ) {
        content()
    }
}
