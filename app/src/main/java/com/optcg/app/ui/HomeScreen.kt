package com.optcg.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.container
import com.optcg.app.data.PrintingEntity
import com.optcg.app.data.SetProgress
import java.time.LocalDate
import kotlin.random.Random

@Composable
fun HomeScreen(nav: Nav) {
    val container = LocalContext.current.container
    val berries by container.simStats.berries.collectAsStateWithLifecycle()
    val sets by remember { container.db.catalog().setProgress() }.collectAsStateWithLifecycle(emptyList())
    val profiles = remember(sets) { container.catalog.pullProfiles() }
    // Sets with a simulator, newest first, as booster boxes.
    val kindOrder = listOf("booster", "extra", "premium")
    val boxes = sets.filter { profiles.profileFor(it.id, it.code, it.kind) != null && it.total > 0 }
        .sortedBy { kindOrder.indexOf(it.kind).let { i -> if (i < 0) kindOrder.size else i } }
    var featured by remember { mutableStateOf<List<PrintingEntity>>(emptyList()) }

    LaunchedEffect(boxes.take(2).map { it.id }) {
        if (boxes.isEmpty()) return@LaunchedEffect
        // Five hits from the two newest sets, changing once a day.
        val pool = boxes.take(2).flatMap { container.db.catalog().printingsInSet(it.id) }
            .filter { it.kind == "card" && it.status == "official" && (it.rarity in setOf("SEC", "SR", "SP") || it.isParallel) }
        featured = pool.shuffled(Random(LocalDate.now().toEpochDay())).take(5)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Palette.Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Featured Cards", fontSize = 30.sp, color = Palette.Text, modifier = Modifier.weight(1f))
            BerryIcon(22.dp)
            Text("%,d".format(berries), fontSize = 20.sp, color = Palette.Text, modifier = Modifier.padding(start = 6.dp))
            IconButton(onClick = nav::settings) { Icon(Icons.Filled.Settings, "Settings", tint = Palette.Unselected) }
        }
        if (featured.isNotEmpty()) {
            val pager = rememberPagerState { featured.size }
            HorizontalPager(pager, Modifier.fillMaxWidth().height(250.dp).padding(top = 8.dp)) { page ->
                FeaturedCard(featured[page], sets) { nav.card(featured[page].id) }
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.Center) {
                repeat(featured.size) { i ->
                    Box(
                        Modifier
                            .padding(horizontal = 6.dp)
                            .size(if (i == pager.currentPage) 9.dp else 6.dp)
                            .background(if (i == pager.currentPage) Palette.Text else Palette.Unselected.copy(alpha = 0.6f), CircleShape),
                    )
                }
            }
        } else {
            Spacer(Modifier.height(276.dp))
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().height(330.dp).padding(top = 12.dp),
        ) {
            items(boxes, key = { it.id }) { set ->
                Column(
                    Modifier.width(190.dp).fillMaxHeight().clickable { nav.sim(set.id) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BoxArt(set.code, setDisplayName(set), Modifier.weight(1f))
                    Text(set.code ?: "", color = Palette.SubText, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun FeaturedCard(card: PrintingEntity, sets: List<SetProgress>, onClick: () -> Unit) {
    val set = sets.firstOrNull { it.id == card.setId }
    Row(Modifier.fillMaxSize().padding(horizontal = 8.dp).clickable(onClick = onClick)) {
        CardImage(card, label = card.displayName, detail = true, modifier = Modifier.fillMaxHeight())
        Column(Modifier.padding(start = 14.dp).weight(1f)) {
            Text(card.code, fontSize = 17.sp, color = Palette.Text)
            Text(card.displayName, fontSize = 22.sp, color = Palette.Text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(card.rarity.orEmpty() + if (card.isParallel) " Parallel" else "", fontSize = 15.sp, color = Palette.Text)
            Text(
                card.effectEn ?: card.effectJp ?: "",
                fontSize = 13.sp,
                color = Palette.Text,
                lineHeight = 16.sp,
                maxLines = 7,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (set != null) {
                Text(set.nameEn ?: set.nameJp, fontSize = 13.sp, color = Palette.Text, maxLines = 2, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

fun setDisplayName(set: SetProgress): String = set.nameEn?.let(::shortEnglishSetName) ?: shortJapaneseSetName(set.nameJp)
