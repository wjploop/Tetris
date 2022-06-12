package com.wjploop.tetris

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.wjploop.tetris.ext.logx
import com.wjploop.tetris.ui.gamer.Game
import com.wjploop.tetris.ui.material.SoundWidget
import com.wjploop.tetris.ui.panel.HomePage
import com.wjploop.tetris.ui.theme.TetrisTheme


class MainActivity : ComponentActivity() {


    lateinit var reviewManager: ReviewManager

    companion object{
        @JvmStatic
        lateinit var instace: MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainContainer = WindowInsetsControllerCompat(window,window.decorView)
        mainContainer.hide(WindowInsetsCompat.Type.systemBars())
        mainContainer.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        instace = this

        reviewManager = ReviewManagerFactory.create(this)


        setContent {
            TetrisTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    SoundWidget {
                        Game {
                            HomePage()
                        }
                    }

                }
            }
        }
    }

    fun review() {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener {
            logx("request complete:  ${it.result}, ${it.exception.toString()} ")
            if (it.isSuccessful) {
                val reviewInfo = it.result
                if (reviewInfo != null) {
                    reviewManager.launchReviewFlow(this, reviewInfo)

                }
            }
        }
    }

    fun share() {
        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("app link", "https://play.google.com/store/apps/details?id=com.wjploop.tetris")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this,getString(R.string.msg_link_has_been_copyed),Toast.LENGTH_LONG).show()
    }
}