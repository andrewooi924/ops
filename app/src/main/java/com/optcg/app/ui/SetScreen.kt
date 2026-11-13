package com.optcg.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.container
import com.optcg.app.data.CardRow
import com.optcg.app.data.SetEntity
import com.optcg.app.sim.PullSimulator
import com.optcg.app.sim.simPool

private enum class Filter(val label: String) { ALL("All"), OWNED("Owned"), MISSING("Missing"), PARALLEL("Parallels") }

private val RARITY_ORDER = listOf("C", "UC", "R", "SR", "L", "SEC", "SP", "TR", "P", "DON")

/** The original per-set collection page: banner, title, completion ring and rarity counts, then the cards. */
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
    val title = set?.let { s -> s.nameEn?.let(::shortEnglishSetName) ?: shortJapaneseSetName(s.nameJp) }.orEmpty()

    LazyVerticalGrid(
        GridCells.Fixed(3),
        contentPadding = PaddingValues(start = 4.dp, end = 4.dp, bottom = 12.dp),
        modifier = Modifier.fillMaxSize().background(Palette.Background).statusBarsPadding(),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                IconButton(onClick = nav::back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Palette.Text) }
                SetHeader(set, title, cards)
                Row(
                    Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (canSimulate) Pill("Open packs") { nav.sim(setId) }
                    val withImages = cards.count { container.images.hasImage(it) }
                    val offline = cards.count { it.id in saved }
                    when {
                        withImages == 0 -> {}
                        download != null -> Text("Saving ${download!!.done}/${download!!.total}", color = Palette.SubText, fontSize = 12.sp)
                        offline >= withImages -> Text("Saved offline", color = Palette.SubText, fontSize = 12.sp)
                        else -> Pill("Save offline", outlined = true) { container.jobs.saveSetImages(setId) }
                    }
                }
                Row(
                    Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Filter.entries.forEach { option ->
                        Pill(option.label, outlined = filter != option) { filter = option }
                    }
                }
            }
        }
        items(shown, key = { it.id }) { card ->
            CardTile(card, onClick = { nav.card(card.id) }, dimUnowned = true)
        }
    }
}

@Composable
private fun SetHeader(set: SetEntity?, title: String, cards: List<CardRow>) {
    val owned = cards.count { it.owned > 0 }
    val percent = if (cards.isEmpty()) 0 else owned * 100 / cards.size
    Row(Modifier.fillMaxWidth().padding(end = 8.dp)) {
        SetBanner(set?.code, title, Modifier.width(140.dp))
        Column(Modifier.padding(start = 14.dp).weight(1f)) {
            Text(
                listOfNotNull(set?.code?.replace("-", ""), title).joinToString(" – "),
                color = Palette.Text,
                fontSize = 18.sp,
                lineHeight = 20.sp,
            )
            Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(percent)
                RarityCounts(cards, Modifier.padding(start = 12.dp))
            }
        }
    }
}

@Composable
private fun ProgressRing(percent: Int) {
    Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(64.dp)) {
            val stroke = 5.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(Palette.Unselected.copy(alpha = 0.3f), 0f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(stroke))
            drawArc(Palette.Selected, -90f, 360f * percent / 100f, false, Offset(inset, inset), arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Text("$percent%", color = Palette.Text, fontSize = 14.sp)
    }
}

@Composable
private fun RarityCounts(cards: List<CardRow>, modifier: Modifier = Modifier) {
    // Base cards by rarity, then all parallels together, as owned/total.
    val groups = cards.groupBy { if (it.variant != null) "★" else it.rarity ?: "?" }
    val keys = groups.keys.sortedBy { RARITY_ORDER.indexOf(it).let { i -> if (i < 0) 99 else i } }
    Column(modifier) {
        keys.chunked(3).forEach { row ->
            Row {
                row.forEach { key ->
                    val list = groups.getValue(key)
                    Column(Modifier.width(46.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${list.count { it.owned > 0 }}/${list.size}", color = Palette.Text, fontSize = 11.sp)
                        Text(key, color = Palette.Text, fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

/** Rounded purple button like the simulator's Exit / Next. */
@Composable
fun Pill(text: String, outlined: Boolean = false, onClick: () -> Unit) {
    Box(
        Modifier
            .background(if (outlined) Palette.Surface else Palette.Button, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) { Text(text, color = Palette.Text, fontSize = 13.sp) }
}
