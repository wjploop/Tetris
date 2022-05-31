package com.wjploop.tetris.ui.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wjploop.tetris.MainActivity
import com.wjploop.tetris.R

@Composable
fun HomePage() {
    val screenW = LocalConfiguration.current.screenWidthDp * 0.8

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(Color(0xffefcc19))
    }

    var showMenu by remember {
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
                    showMenu = !showMenu;
                })
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        MainActivity.instace.share()
                    }) {
                    Text(text = stringResource(R.string.share))
                }
                Divider(modifier = Modifier.padding(horizontal = 12.dp))
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        MainActivity.instace.review()
                    }) {
                    Text(text = stringResource(R.string.feedback))
                }
            }
        }

        ScreenDecoration {
            Screen(width = screenW.dp)
        }
        GameController()
    }
}

@Preview
@Composable
fun PreviewHomePage() {
    HomePage()
}