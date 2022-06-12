package com.wjploop.tetris.ui.panel

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wjploop.tetris.MainActivity
import com.wjploop.tetris.R
import com.wjploop.tetris.ui.gamer.Gamer
import com.wjploop.tetris.ui.gamer.LocalGamer
import com.wjploop.tetris.ui.theme.Purple500

@Composable
fun HomePage(
    gamer: Gamer = LocalGamer.current
) {
    val screenW = LocalConfiguration.current.screenWidthDp * 0.8

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(Color(0xffefcc19))
    }

    var showMenu: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(color = Color(0xffefcc19)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {


        Box(
            modifier = Modifier.align(Alignment.End),
        ) {
            IconButton(
                content = {
                    Icon(painter = painterResource(id = R.drawable.more), contentDescription = "")
                },
                onClick = {
                    gamer.pause()
                    showMenu.value = !showMenu.value
                })

            if (showMenu.value) {
                Dialog(onDismissRequest = { showMenu.value = false }) {
                    AboutDialog(modifier = Modifier, showMenu = showMenu)
                }
            }

        }

        ScreenDecoration {
            Screen(width = screenW.dp)
        }
        GameController()
    }

}

@Composable
fun AboutDialog(modifier: Modifier, showMenu: MutableState<Boolean>) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier
                .background(
                    Color.White
                )
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(6.dp),
                onClick = { showMenu.value = false}) {
                Icon(Icons.Filled.Close, contentDescription = "")
            }


            androidx.compose.material3.Text(
                modifier = modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.body2,
                text = stringResource(id = R.string.msg_about), textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(Color(0xFF2196F3)),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                androidx.compose.material3.TextButton(onClick = {
                    showMenu.value = false
                    MainActivity.instace.share()
                }) {

                    androidx.compose.material3.Text(
                        stringResource(R.string.share),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                androidx.compose.material3.TextButton(onClick = {
                    showMenu.value = false
                    MainActivity.instace.review()
                }) {
                    androidx.compose.material3.Text(
                        stringResource(R.string.feedback),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }

        }

    }
}


@Preview
@Composable
fun PreviewHomePage() {
    HomePage()
}