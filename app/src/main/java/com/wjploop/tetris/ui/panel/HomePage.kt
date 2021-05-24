package com.wjploop.tetris.ui.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomePage() {
    val screenW = LocalConfiguration.current.screenWidthDp * 0.8

    Column(
        Modifier
            .fillMaxSize()
            .background(color = Color(0xffefcc19)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        TextButton(modifier = Modifier.align(Alignment.End), onClick = {

        }) {
            Text("...")
        }
        Screen(width = screenW.dp)
        GameController()

    }
}

@Preview
@Composable
fun PreviewHomePage() {
    HomePage()
}