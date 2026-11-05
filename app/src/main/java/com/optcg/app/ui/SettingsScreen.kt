package com.optcg.app.ui

import android.text.format.DateUtils
import android.text.format.Formatter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.BuildConfig
import com.optcg.app.catalog.UpdateResult
import com.optcg.app.container
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val container = context.container
    val info by container.catalog.info.collectAsStateWithLifecycle()
    val saved by container.images.saved.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var checking by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    var keepOffline by remember { mutableStateOf(container.collection.keepCollectionOffline) }
    var bytes by remember { mutableLongStateOf(0L) }
    LaunchedEffect(saved.size) { bytes = withContext(Dispatchers.IO) { container.images.bytesUsed() } }

    Column(
        Modifier
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)

        Heading("Card catalog")
        info?.let {
            Text("Version ${it.version}", style = MaterialTheme.typography.bodyMedium)
            Text("Built ${it.generatedAt.take(10)} · ${if (it.source == "bundled") "came with the app" else "downloaded"}", style = MaterialTheme.typography.bodySmall)
            if (it.checkedAt > 0) {
                Text("Last checked ${DateUtils.getRelativeTimeSpanString(it.checkedAt)}", style = MaterialTheme.typography.bodySmall)
            }
        }
        Text(
            "Checked for new cards once a day when you're online. Everything else works offline.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp),
        )
        Button(
            enabled = !checking,
            onClick = {
                checking = true
                result = null
                scope.launch {
                    result = when (val outcome = container.catalog.checkForUpdate()) {
                        UpdateResult.UpToDate -> "You have the latest cards."
                        is UpdateResult.Updated -> "Updated to ${outcome.version}."
                        UpdateResult.NeedsAppUpdate -> "A newer catalog needs an app update."
                        UpdateResult.NotConfigured -> "Updates aren't set up in this build."
                        is UpdateResult.Failed -> "Couldn't check: ${outcome.message}"
                    }
                    checking = false
                }
            },
        ) { Text(if (checking) "Checking…" else "Check for new cards") }
        result?.let { Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp)) }

        Heading("Offline images")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Keep my collection offline", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Saves images of cards you own or wishlist.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = keepOffline, onCheckedChange = {
                keepOffline = it
                container.collection.keepCollectionOffline = it
            })
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "${saved.size} images saved · ${Formatter.formatShortFileSize(context, bytes)}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "Save a whole set from its page. Other images load when you view them.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(
            onClick = { scope.launch { container.images.clear() } },
            enabled = saved.isNotEmpty(),
            modifier = Modifier.padding(top = 4.dp),
        ) { Text("Delete offline images") }

        Heading("Pull rates")
        Text(
            "Bandai doesn't publish pull rates. The simulator uses estimates from Japanese box openings, " +
                "so treat its results as approximate. Each set's estimates are shown before you open packs.",
            style = MaterialTheme.typography.bodySmall,
        )

        Heading("About")
        Text("PieceLab ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)
        Text(
            "Card data and images are from the official ONE PIECE CARD GAME websites. Not affiliated with Bandai.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Heading(text: String) {
    Spacer(Modifier.height(20.dp))
    HorizontalDivider()
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
}
