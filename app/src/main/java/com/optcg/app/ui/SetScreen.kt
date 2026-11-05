package com.optcg.app.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.container
import com.optcg.app.data.SetEntity
import com.optcg.app.sim.PullSimulator
import com.optcg.app.sim.simPool
import com.optcg.app.work.Jobs

private enum class Filter(val label: String) { ALL("All"), OWNED("Owned"), MISSING("Missing"), PARALLEL("Parallels") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetScreen(setId: String, nav: Nav) {
    val container = LocalContext.current.container
    val cards by remember(setId) { container.db.catalog().cardsInSet(setId) }.collectAsStateWithLifecycle(emptyList())
    val saved by container.images.saved.collectAsStateWithLifecycle()
    val download by remember(setId) { container.jobs.setDownload(setId) }.collectAsStateWithLifecycle(null)
    var set by remember { mutableStateOf<SetEntity?>(null) }
    var canSimulate by remember { mutableStateOf(false) }
    var filter by rememberSaveable { mutableStateOf(Filter.ALL) }

    LaunchedEffect(setId) {
        val loaded = container.db.catalog().set(setId)
        set = loaded
        val profile = loaded?.let { container.catalog.pullProfiles().profileFor(it.id, it.code, it.kind) }
        canSimulate = profile != null && PullSimulator(profile, simPool(container.db.catalog().printingsInSet(setId))).canSimulate
    }

    // The set's own cards first (OP10-xxx in OP-10), then reprints and SPs from other sets.
    val prefix = set?.code?.replace("-", "")?.plus("-")
    val ordered = if (prefix == null) cards else cards.sortedBy { !it.code.startsWith(prefix) }
    val shown = when (filter) {
        Filter.ALL -> ordered
        Filter.OWNED -> ordered.filter { it.owned > 0 }
        Filter.MISSING -> ordered.filter { it.owned == 0 }
        Filter.PARALLEL -> ordered.filter { it.variant != null }
    }
    val withImages = cards.count { container.images.hasImage(it) }
    val offline = cards.count { it.id in saved }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(set?.code ?: set?.nameEn ?: set?.nameJp ?: "", maxLines = 1)
                        set?.let {
                            Text(
                                it.nameEn?.let(::shortEnglishSetName) ?: shortJapaneseSetName(it.nameJp),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                navigationIcon = { IconButton(onClick = nav::back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = {
                    if (canSimulate) {
                        TextButton(onClick = { nav.sim(setId) }) {
                            Icon(Icons.Filled.PlayArrow, null)
                            Text("Open packs")
                        }
                    }
                },
            )
        },
    ) { padding ->
        CardGrid(shown, onCard = nav::card, modifier = Modifier.padding(padding)) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(Modifier.padding(horizontal = 4.dp)) {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Filter.entries.forEach { option ->
                            FilterChip(selected = filter == option, onClick = { filter = option }, label = { Text(option.label) })
                        }
                    }
                    OfflineRow(
                        offline = offline,
                        available = withImages,
                        progress = download,
                        onDownload = { container.jobs.saveSetImages(setId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun OfflineRow(offline: Int, available: Int, progress: Jobs.Progress?, onDownload: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        val text = when {
            available == 0 -> "No images for these cards yet"
            progress?.waitingForNetwork == true -> "Offline download waiting for a connection"
            progress != null -> "Downloading images ${progress.done}/${progress.total}"
            offline >= available -> "All images saved for offline use"
            else -> "$offline of $available images saved offline"
        }
        Text(text, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        when {
            progress != null -> CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            available > 0 && offline >= available -> Icon(Icons.Filled.Check, "Saved", Modifier.size(20.dp))
            available > 0 -> TextButton(onClick = onDownload) { Text("Save offline") }
        }
    }
}
