package com.wjploop.tetris.ui.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomePage() {
    val screenW = LocalConfiguration.current.screenWidthDp * 0.8

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextButton(modifier = Modifier.align(Alignment.End), onClick = {

        }) {
            Text("打钱")
        }
        Screen(width = screenW.dp)
    }

}

@Preview
@Composable
fun PreviewHomePage() {
    HomePage()
}