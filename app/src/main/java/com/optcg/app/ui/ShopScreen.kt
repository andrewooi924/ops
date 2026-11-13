package com.optcg.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** As in the original app, the shop isn't built yet. */
@Composable
fun ShopScreen() {
    Box(Modifier.fillMaxSize().background(Palette.Background).statusBarsPadding().padding(8.dp)) {
        Text("COMING SOON", color = Palette.SubText, fontSize = 14.sp)
    }
}
