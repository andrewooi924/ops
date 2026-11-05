package com.optcg.app.ui

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.optcg.app.catalog.PullProfile
import com.optcg.app.container
import com.optcg.app.data.PrintingEntity
import com.optcg.app.sim.Box as SimBox
import com.optcg.app.sim.Pull
import com.optcg.app.sim.PullSimulator
import com.optcg.app.sim.Slot
import com.optcg.app.sim.simPool
import kotlinx.coroutines.launch

/** What was just opened: one pack revealed card by card, or a whole box. */
sealed interface Opened {
    val pulls: List<Pull>

    data class OnePack(override val pulls: List<Pull>, val revealed: Int) : Opened
    data class WholeBox(val box: SimBox) : Opened {
        override val pulls get() = box.pulls
    }
}

class SimViewModel(application: Application, private val setId: String) : AndroidViewModel(application) {
    private val container = application.container
    var title by mutableStateOf("")
        private set
    var profile by mutableStateOf<PullProfile?>(null)
        private set
    var cards by mutableStateOf<Map<String, PrintingEntity>>(emptyMap())
        private set
    var opened by mutableStateOf<Opened?>(null)
        private set
    var added by mutableStateOf(false)
        private set
    private var simulator: PullSimulator? = null

    init {
        viewModelScope.launch {
            val set = container.db.catalog().set(setId) ?: return@launch
            val printings = container.db.catalog().printingsInSet(setId)
            title = set.code ?: set.nameEn ?: set.nameJp
            cards = printings.associateBy { it.id }
            profile = container.catalog.pullProfiles().profileFor(set.id, set.code, set.kind)
            simulator = profile?.let { PullSimulator(it, simPool(printings)) }?.takeIf { it.canSimulate }
        }
    }

    fun openPack() {
        val sim = simulator ?: return
        opened = Opened.OnePack(sim.openPack().cards, revealed = 0)
        added = false
    }

    fun openBox() {
        val sim = simulator ?: return
        val box = sim.openBox()
        opened = Opened.WholeBox(box)
        added = false
    }

    fun reveal(all: Boolean) {
        val pack = opened as? Opened.OnePack ?: return
        opened = pack.copy(revealed = if (all) pack.pulls.size else (pack.revealed + 1).coerceAtMost(pack.pulls.size))
    }

    fun addToCollection() {
        val pulls = opened?.pulls ?: return
        added = true
        viewModelScope.launch { container.collection.add(pulls.map { it.card.id }) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimScreen(setId: String, nav: Nav) {
    val vm: SimViewModel = viewModel(key = "sim-$setId") {
        SimViewModel(this[androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!, setId)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Open ${vm.title}") },
                navigationIcon = { IconButton(onClick = nav::back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
            )
        },
    ) { padding ->
        val profile = vm.profile
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = vm::openPack, enabled = profile != null) { Text("Open a pack") }
                OutlinedButton(onClick = vm::openBox, enabled = profile != null) {
                    Text("Open a box (${profile?.packsPerBox ?: 0})")
                }
            }
            AnimatedContent(targetState = vm.opened, label = "opened", modifier = Modifier.weight(1f)) { opened ->
                when (opened) {
                    null -> RatesInfo(profile)
                    is Opened.OnePack -> PackView(opened, vm, nav)
                    is Opened.WholeBox -> BoxView(opened.box, vm, nav)
                }
            }
        }
    }
}

@Composable
private fun RatesInfo(profile: PullProfile?) {
    if (profile == null) return
    Column(Modifier.padding(16.dp)) {
        Card {
            Column(Modifier.padding(16.dp)) {
                Text("Estimated pull rates", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Bandai doesn't publish pull rates. These come from Japanese box openings and are approximate.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                Text("${profile.packsPerBox} packs per box, ${profile.cardsPerPack} cards per pack", style = MaterialTheme.typography.bodyMedium)
                profile.perBox.forEach { (rarity, range) ->
                    Text("$rarity: ${range.first()}–${range.last()} per box", style = MaterialTheme.typography.bodyMedium)
                }
                profile.boxPatterns.forEach { pattern ->
                    val what = when {
                        pattern.sec > 0 && pattern.parallel > 0 -> "${pattern.sec} SEC + ${pattern.parallel} parallel"
                        pattern.sec > 0 -> "${pattern.sec} SEC"
                        else -> "${pattern.parallel} parallel${if (pattern.parallel == 1) "" else "s"}"
                    }
                    Text("${pattern.weight}% of boxes: $what", style = MaterialTheme.typography.bodyMedium)
                }
                profile.specialRares?.perCase?.let { perCase ->
                    val amount = if (perCase % 1.0 == 0.0) perCase.toInt().toString() else perCase.toString()
                    Text(
                        "${profile.specialRares.rarities.joinToString("/")}: about $amount per case of ${profile.boxesPerCase} boxes",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun PackView(pack: Opened.OnePack, vm: SimViewModel, nav: Nav) {
    LazyVerticalGrid(GridCells.Fixed(3), contentPadding = PaddingValues(12.dp), modifier = Modifier.fillMaxSize()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val done = pack.revealed >= pack.pulls.size
                Text(
                    if (done) "Pack opened" else "Tap the next card to reveal it",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )
                if (!done) TextButton(onClick = { vm.reveal(all = true) }) { Text("Reveal all") }
                else AddButton(vm)
            }
        }
        itemsIndexed(pack.pulls) { index, pull ->
            val shown = index < pack.revealed
            Box(Modifier.padding(4.dp)) {
                if (shown) {
                    PullTile(pull, vm.cards[pull.card.id], nav)
                } else {
                    FaceDown(onClick = { if (index == pack.revealed) vm.reveal(all = false) })
                }
            }
        }
    }
}

@Composable
private fun BoxView(box: SimBox, vm: SimViewModel, nav: Nav) {
    val hits = box.hits
    val counts = hits.groupingBy { it.slot }.eachCount()
    LazyVerticalGrid(GridCells.Adaptive(104.dp), contentPadding = PaddingValues(12.dp), modifier = Modifier.fillMaxSize()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${box.packs.size} packs · ${box.pulls.size} cards", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    AddButton(vm)
                }
                Text(
                    Slot.entries.reversed().filter { it != Slot.FILLER && it in counts }
                        .joinToString(" · ") { "${counts.getValue(it)} ${slotName(it, counts.getValue(it))}" },
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text("Hits, best first", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
            }
        }
        itemsIndexed(hits) { _, pull -> Box(Modifier.padding(4.dp)) { PullTile(pull, vm.cards[pull.card.id], nav) } }
    }
}

@Composable
private fun AddButton(vm: SimViewModel) {
    TextButton(onClick = vm::addToCollection, enabled = !vm.added) {
        Text(if (vm.added) "Added to collection" else "Add to collection")
    }
}

@Composable
private fun PullTile(pull: Pull, printing: PrintingEntity?, nav: Nav) {
    Column(Modifier.clickable { nav.card(pull.card.id) }) {
        if (printing != null) {
            CardImage(printing, label = "${printing.code}\n${printing.displayName}", modifier = Modifier.fillMaxWidth())
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(printing.code, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                RarityLabel(printing.rarity, printing.isParallel)
            }
            Text(printing.displayName, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}

@Composable
private fun FaceDown(onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth().aspectRatio(CARD_RATIO).clickable(onClick = onClick),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

private fun slotName(slot: Slot, count: Int): String {
    val plural = if (count == 1) "" else "s"
    return when (slot) {
        Slot.SPECIAL -> "special rare$plural"
        Slot.PARALLEL -> "parallel$plural"
        Slot.SEC -> "SEC"
        Slot.SR -> "SR"
        Slot.L -> "leader$plural"
        Slot.R -> "R"
        Slot.FILLER -> "C/UC"
    }
}
