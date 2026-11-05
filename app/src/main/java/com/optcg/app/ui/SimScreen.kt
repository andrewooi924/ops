package com.optcg.app.ui

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.imageLoader
import coil.request.ImageRequest
import com.optcg.app.R
import com.optcg.app.catalog.PullProfile
import com.optcg.app.container
import com.optcg.app.data.PrintingEntity
import com.optcg.app.data.SetEntity
import com.optcg.app.sim.Box as SimBox
import com.optcg.app.sim.Pack
import com.optcg.app.sim.Pull
import com.optcg.app.sim.PullSimulator
import com.optcg.app.sim.Slot
import com.optcg.app.sim.berriesFor
import com.optcg.app.sim.simPool
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Packs in a row without a parallel, SEC or special rare before pity kicks in (as in the original). */
private const val PITY_PACKS = 24

sealed interface SimStage {
    data object Loading : SimStage
    data object Unavailable : SimStage
    data class Sealed(val pack: Pack) : SimStage
    data class Revealing(val pack: Pack, val isNew: List<Boolean>) : SimStage
    data class PackDone(val pack: Pack, val isNew: List<Boolean>, val berries: Int) : SimStage
    data class BoxDone(val box: SimBox, val hits: List<Pull>, val hitIsNew: List<Boolean>, val newCount: Int, val berries: Int) : SimStage
}

class SimViewModel(application: Application, private val setId: String) : AndroidViewModel(application) {
    private val container = application.container
    var set by mutableStateOf<SetEntity?>(null)
        private set
    var profile by mutableStateOf<PullProfile?>(null)
        private set
    var cards by mutableStateOf<Map<String, PrintingEntity>>(emptyMap())
        private set
    var stage by mutableStateOf<SimStage>(SimStage.Loading)
        private set
    var packsOpened by mutableIntStateOf(container.simStats.packsOpened(setId))
        private set
    private var simulator: PullSimulator? = null

    init {
        viewModelScope.launch {
            val loaded = container.db.catalog().set(setId)
            val printings = container.db.catalog().printingsInSet(setId)
            set = loaded
            cards = printings.associateBy { it.id }
            profile = loaded?.let { container.catalog.pullProfiles().profileFor(it.id, it.code, it.kind) }
            simulator = profile?.let { PullSimulator(it, simPool(printings)) }?.takeIf { it.canSimulate }
            if (simulator == null) stage = SimStage.Unavailable else prepare()
        }
    }

    /** Seals the next pack, applying pity, and starts loading its images while it's being torn. */
    private fun prepare() {
        val sim = simulator ?: return
        var pack = sim.openPack()
        if (container.simStats.dryStreak(setId) >= PITY_PACKS - 1) pack = sim.forceHit(pack)
        prefetch(pack.cards)
        stage = SimStage.Sealed(pack)
    }

    fun packOpened() {
        val sealed = stage as? SimStage.Sealed ?: return
        viewModelScope.launch {
            val (isNew, berries) = commit(sealed.pack.cards)
            container.simStats.addPacks(setId, 1)
            packsOpened += 1
            val hit = sealed.pack.cards.any { it.slot.isBigHit }
            container.simStats.setDryStreak(setId, if (hit) 0 else container.simStats.dryStreak(setId) + 1)
            stage = SimStage.Revealing(sealed.pack, isNew)
            pendingBerries = berries
        }
    }

    private var pendingBerries = 0

    fun revealed() {
        val revealing = stage as? SimStage.Revealing ?: return
        stage = SimStage.PackDone(revealing.pack, revealing.isNew, pendingBerries)
    }

    fun nextPack() = prepare()

    fun openBox() {
        val sim = simulator ?: return
        stage = SimStage.Loading
        viewModelScope.launch {
            val box = sim.openBox()
            val pulls = box.pulls
            val (isNew, berries) = commit(pulls)
            val newById = pulls.indices.filter { isNew[it] }.map { pulls[it] }.toSet()
            container.simStats.addPacks(setId, box.packs.size)
            packsOpened += box.packs.size
            container.simStats.setDryStreak(setId, 0)
            val hits = box.hits
            prefetch(hits)
            stage = SimStage.BoxDone(box, hits, hits.map { it in newById }, isNew.count { it }, berries)
        }
    }

    /**
     * Adds pulls to the collection, as the original simulator did. Returns which pulls were new
     * and the berries earned for duplicates.
     */
    private suspend fun commit(pulls: List<Pull>): Pair<List<Boolean>, Int> {
        val owned = container.db.catalog().cardsInSet(setId).first().associate { it.id to it.owned }
        val seen = HashSet<String>()
        var berries = 0
        val isNew = pulls.map { pull ->
            val id = pull.card.id
            val fresh = (owned[id] ?: 0) == 0 && seen.add(id)
            if (!fresh) berries += berriesFor(pull.slot, pull.card.rarity, pull.card.isParallel)
            fresh
        }
        container.collection.add(pulls.map { it.card.id })
        container.simStats.addBerries(berries)
        return isNew to berries
    }

    private fun prefetch(pulls: List<Pull>) {
        val context = getApplication<Application>()
        pulls.mapNotNull { cards[it.card.id] }.distinctBy { it.id }.forEach { printing ->
            container.images.detailModel(printing)?.let { model ->
                context.imageLoader.enqueue(ImageRequest.Builder(context).data(model).build())
            }
        }
    }
}

@Composable
fun SimScreen(setId: String, nav: Nav) {
    val vm: SimViewModel = viewModel(key = "sim-$setId") {
        SimViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!, setId)
    }
    var inspecting by remember { mutableStateOf<PrintingEntity?>(null) }
    val set = vm.set
    val title = set?.let { s -> s.nameEn?.let(::shortEnglishSetName) ?: shortJapaneseSetName(s.nameJp) }.orEmpty()
    val base = SetArt.gradient(set?.code)

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(base, Color.Black), radius = 2200f)),
    ) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            Box(Modifier.fillMaxWidth()) {
                IconButton(onClick = nav::back, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Exit", tint = Color.White)
                }
                // The original's big packs-opened counter.
                Text(
                    "${vm.packsOpened}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            AnimatedContent(
                targetState = vm.stage,
                contentKey = { it::class },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "stage",
                modifier = Modifier.weight(1f),
            ) { stage ->
                when (stage) {
                    SimStage.Loading -> Box(Modifier.fillMaxSize())
                    SimStage.Unavailable -> Message("There's no simulator for this set.")
                    is SimStage.Sealed -> SealedPack(set?.code, title, onOpened = vm::packOpened, onBox = vm::openBox, packsPerBox = vm.profile?.packsPerBox ?: 24)
                    is SimStage.Revealing -> CardStack(
                        stage.pack.cards, vm.cards, stage.isNew,
                        modifier = Modifier.fillMaxSize().padding(bottom = 24.dp),
                        onDone = vm::revealed,
                    )
                    is SimStage.PackDone -> PackResults(stage, vm.cards, onInspect = { inspecting = it }, onExit = nav::back, onNext = vm::nextPack)
                    is SimStage.BoxDone -> BoxResults(
                        stage, vm.cards, onInspect = { inspecting = it }, onExit = nav::back,
                        onAnotherBox = vm::openBox, onPack = vm::nextPack,
                    )
                }
            }
        }
        inspecting?.let { card ->
            InspectOverlay(card, onClose = { inspecting = null }, onOpenPage = { inspecting = null; nav.card(card.id) })
        }
    }
}

@Composable
private fun SealedPack(code: String?, title: String, packsPerBox: Int, onOpened: () -> Unit, onBox: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.weight(1f).fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            TearablePack(code, title, Modifier.fillMaxHeight(0.9f), onTearStarted = { started = true }, onOpened = onOpened)
        }
        Text(
            if (started) " " else "Swipe across the top to tear it open, or tap",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp,
        )
        Row(Modifier.padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Pill("Open a whole box ($packsPerBox packs)", outlined = true, onClick = onBox)
        }
    }
}

@Composable
private fun PackResults(
    stage: SimStage.PackDone,
    cards: Map<String, PrintingEntity>,
    onInspect: (PrintingEntity) -> Unit,
    onExit: () -> Unit,
    onNext: () -> Unit,
) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        LazyVerticalGrid(GridCells.Fixed(3), contentPadding = PaddingValues(horizontal = 14.dp), userScrollEnabled = false) {
            itemsIndexed(stage.pack.cards) { index, pull ->
                ResultCard(pull, cards[pull.card.id], stage.isNew[index], onInspect)
            }
        }
        if (stage.berries > 0) BerriesEarned(stage.berries)
        Row(Modifier.padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(40.dp)) {
            Pill("Exit", onClick = onExit)
            Pill("Next", onClick = onNext)
        }
        Text("Tap a card to inspect it", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp))
    }
}

@Composable
private fun BoxResults(
    stage: SimStage.BoxDone,
    cards: Map<String, PrintingEntity>,
    onInspect: (PrintingEntity) -> Unit,
    onExit: () -> Unit,
    onAnotherBox: () -> Unit,
    onPack: () -> Unit,
) {
    val counts = stage.hits.groupingBy { it.slot }.eachCount()
    LazyVerticalGrid(GridCells.Fixed(3), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp), modifier = Modifier.fillMaxSize()) {
        item(span = { GridItemSpan(3) }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 10.dp)) {
                Text("${stage.box.packs.size} packs · ${stage.box.pulls.size} cards · ${stage.newCount} new", color = Color.White, fontSize = 16.sp)
                Text(
                    Slot.entries.reversed().filter { it != Slot.FILLER && it in counts }
                        .joinToString(" · ") { "${counts.getValue(it)} ${slotName(it, counts.getValue(it))}" },
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp),
                )
                if (stage.berries > 0) BerriesEarned(stage.berries)
                Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Pill("Exit", onClick = onExit)
                    Pill("Another box", onClick = onAnotherBox)
                    Pill("Single pack", outlined = true, onClick = onPack)
                }
            }
        }
        itemsIndexed(stage.hits) { index, pull ->
            ResultCard(pull, cards[pull.card.id], stage.hitIsNew[index], onInspect)
        }
    }
}

@Composable
private fun ResultCard(pull: Pull, printing: PrintingEntity?, isNew: Boolean, onInspect: (PrintingEntity) -> Unit) {
    if (printing == null) return
    Box(Modifier.padding(5.dp).clickable { onInspect(printing) }) {
        CardImage(printing, label = "${printing.code}\n${printing.displayName}", modifier = Modifier.fillMaxWidth())
        if (pull.slot.isBigHit) {
            FoilShine(8f, 14f, Modifier.matchParentSize(), strength = 0.6f)
        }
        if (isNew) {
            Image(painterResource(R.drawable.ic_new), "New", Modifier.align(Alignment.TopStart).size(26.dp))
        }
    }
}

@Composable
private fun BerriesEarned(amount: Int) {
    Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("+", color = Color.White, fontSize = 16.sp)
        BerryIcon(18.dp)
        Text("%,d for duplicates".format(amount), color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun Message(text: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text, color = Color.White, textAlign = TextAlign.Center)
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
