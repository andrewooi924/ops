package com.optcg.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.container
import com.optcg.app.data.SetProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetsScreen(nav: Nav) {
    val container = LocalContext.current.container
    val loaded by remember { container.db.catalog().setProgress() }.collectAsStateWithLifecycle(null)
    val sets = loaded.orEmpty()
    val owned by remember { container.collection.distinctOwned() }.collectAsStateWithLifecycle(0)
    val byKind = sets.groupBy { it.kind }
    val known = SET_SECTIONS.map { it.first }.toSet()
    val sections = SET_SECTIONS.mapNotNull { (kind, title) -> byKind[kind]?.let { title to it } } +
        listOfNotNull(sets.filter { it.kind !in known }.takeIf { it.isNotEmpty() }?.let { "Other" to it })

    Scaffold(topBar = { TopAppBar(title = { Text("PieceLab") }) }) { padding ->
        if (loaded == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        LazyColumn(Modifier.padding(padding)) {
            item {
                Text(
                    "${sets.sumOf { it.total }} cards in the catalog · $owned owned",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
            sections.forEach { (title, items) ->
                item(key = "header-$title") {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp),
                    )
                }
                items(items, key = { it.id }) { set -> SetRow(set) { nav.set(set.id) } }
            }
        }
    }
}

@Composable
private fun SetRow(set: SetProgress, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (set.code != null) {
                Text(set.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                set.nameEn?.let(::shortEnglishSetName) ?: shortJapaneseSetName(set.nameJp),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Text("${set.owned}/${set.total}", style = MaterialTheme.typography.labelMedium)
        }
        if (set.nameEn != null) {
            Text(
                shortJapaneseSetName(set.nameJp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { if (set.total == 0) 0f else set.owned.toFloat() / set.total },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            drawStopIndicator = {},
        )
    }
}

/** "BOOSTER PACK -ROMANCE DAWN- [OP01]" -> "ROMANCE DAWN"; the set code is shown separately. */
fun shortEnglishSetName(name: String): String {
    val withoutCode = name.replace(Regex("""\s*\[[^\]]*]\s*$"""), "")
    val inner = Regex("""-(.+)-""").find(withoutCode)?.groupValues?.get(1) ?: withoutCode
    // Bandai's capitalisation is kept: title-casing breaks names like MONKEY.D.LUFFY and 3D2Y.
    return inner.trim()
}

/** "ブースターパック 世界最強の戦士【OP-17】" -> "世界最強の戦士". */
fun shortJapaneseSetName(name: String): String {
    val withoutCode = name.replace(Regex("""\s*【[^】]*】\s*$"""), "").trim()
    val parts = withoutCode.split(' ', '　', limit = 2)
    return if (parts.size == 2 && parts[1].isNotBlank()) parts[1].trim() else withoutCode
}
