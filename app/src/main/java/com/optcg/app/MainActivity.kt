package com.optcg.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.optcg.app.ui.AppRoot
import com.optcg.app.ui.PieceLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PieceLabTheme {
                Surface(Modifier.fillMaxSize()) {
                    // The first launch unpacks the bundled catalog (a few seconds); later launches skip it.
                    val ready by produceState(false) {
                        container.catalog.ensureInstalled()
                        value = true
                    }
                    if (ready) {
                        AppRoot()
                    } else {
                        // The original splash screen, shown while the catalog loads.
                        Image(
                            painterResource(R.drawable.img_splash_vivi),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}
