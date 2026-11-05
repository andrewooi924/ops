package com.optcg.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.container
import com.optcg.app.data.PrintingEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScreen(id: String, nav: Nav) {
    val container = LocalContext.current.container
    val printing by remember(id) { container.db.catalog().printing(id) }.collectAsStateWithLifecycle(null)
    val owned by remember(id) { container.collection.ownedCount(id) }.collectAsStateWithLifecycle(0)
    val wished by remember(id) { container.collection.isWished(id) }.collectAsStateWithLifecycle(false)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(printing?.let { if (it.kind == "don") it.id else it.id.replace('_', ' ') } ?: "") },
                navigationIcon = { IconButton(onClick = nav::back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = { scope.launch { container.collection.setWished(id, !wished) } }) {
                        if (wished) {
                            Icon(Icons.Filled.Favorite, "Remove from wishlist", tint = MaterialTheme.colorScheme.tertiary)
                        } else {
                            Icon(Icons.Filled.FavoriteBorder, "Add to wishlist")
                        }
                    }
                },
            )
        },
    ) { padding ->
        val card = printing ?: return@Scaffold
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            CardImage(
                card,
                label = card.displayName,
                detail = true,
                modifier = Modifier.fillMaxWidth(0.8f).widthIn(max = 420.dp).align(Alignment.CenterHorizontally),
            )
            if (card.imageQuality == "sample") {
                Text(
                    "Sample image from an announcement; the final card may differ.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            OwnedStepper(owned) { count -> scope.launch { container.collection.setCount(id, count) } }
            Spacer(Modifier.height(16.dp))
            CardDetails(card)
            if (card.kind == "card") OtherPrintings(card, nav)
        }
    }
}

@Composable
private fun OwnedStepper(count: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Owned", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        FilledTonalIconButton(onClick = { onChange(count - 1) }, enabled = count > 0) {
            Text("−", style = MaterialTheme.typography.titleLarge)
        }
        Text("$count", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp))
        FilledTonalIconButton(onClick = { onChange(count + 1) }) { Icon(Icons.Filled.Add, "Add one") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardDetails(card: PrintingEntity) {
    val hasEnglish = card.nameEn != null && card.effectEn != null || card.kind == "don"
    var english by rememberSaveable { mutableStateOf(true) }
    val showEnglish = english && hasEnglish

    val name = (if (showEnglish) card.nameEn else card.nameJp) ?: card.displayName
    Text(name, style = MaterialTheme.typography.headlineSmall)
    val otherName = if (showEnglish) card.nameJp else card.nameEn
    if (otherName != null && otherName != name) {
        Text(otherName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        RarityLabel(card.rarity, card.isParallel)
        card.category?.let { Text(it.lowercase().replaceFirstChar(Char::uppercase), style = MaterialTheme.typography.labelLarge) }
        if (card.status == "announced") Badge("Announced", color = MaterialTheme.colorScheme.secondary)
    }

    if (card.kind == "card") {
        Spacer(Modifier.height(12.dp))
        if (hasEnglish) {
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(selected = english, onClick = { english = true }, shape = SegmentedButtonDefaults.itemShape(0, 2)) { Text("English") }
                SegmentedButton(selected = !english, onClick = { english = false }, shape = SegmentedButtonDefaults.itemShape(1, 2)) { Text("日本語") }
            }
        } else {
            Text(
                "No English printing yet; showing Japanese text.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(8.dp))
        val stats = listOfNotNull(
            card.cost?.let { "Cost" to "$it" },
            card.life?.let { "Life" to "$it" },
            card.power?.let { "Power" to "$it" },
            card.counter?.let { "Counter" to "+$it" },
            card.colors.takeIf { it.isNotBlank() }?.let { "Colour" to it.split(',').joinToString(" / ") { c -> c.replaceFirstChar(Char::uppercase) } },
            card.attributes.takeIf { it.isNotBlank() }?.let { "Attribute" to it.split(',').joinToString(" / ") { a -> a.replaceFirstChar(Char::uppercase) } },
            card.block?.let { "Block" to "$it" },
        )
        stats.forEach { (label, value) -> InfoRow(label, value) }
        val feature = if (showEnglish) card.featureEn else card.featureJp
        val effect = if (showEnglish) card.effectEn else card.effectJp
        val trigger = if (showEnglish) card.triggerEn else card.triggerJp
        feature?.let { InfoRow(if (showEnglish) "Type" else "特徴", it) }
        effect?.takeIf { it.isNotBlank() && it != "-" }?.let { Section(if (showEnglish) "Effect" else "効果", it) }
        trigger?.takeIf { it.isNotBlank() }?.let { Section(if (showEnglish) "Trigger" else "トリガー", it) }
        card.notesEn?.takeIf { showEnglish }?.let { Section("Note", it) }
    }
    card.sourceJp?.let { Section(if (card.kind == "don") "Product" else "Released in", it) }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(96.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Section(title: String, body: String) {
    Spacer(Modifier.height(12.dp))
    Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    Text(body, style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun OtherPrintings(card: PrintingEntity, nav: Nav) {
    val container = LocalContext.current.container
    val others by remember(card.code) { container.db.catalog().printingsOfCode(card.code) }.collectAsStateWithLifecycle(emptyList())
    val rest = others.filter { it.id != card.id }
    if (rest.isEmpty()) return
    Spacer(Modifier.height(16.dp))
    HorizontalDivider()
    Text("Other printings", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(rest, key = { it.id }) { other ->
            CardTile(other, onClick = { nav.card(other.id) }, modifier = Modifier.width(112.dp))
        }
    }
}
