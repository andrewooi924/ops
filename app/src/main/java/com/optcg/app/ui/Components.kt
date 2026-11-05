package com.optcg.app.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.optcg.app.container
import com.optcg.app.data.CardRow
import com.optcg.app.images.HasImage

/** Width / height of a card. */
const val CARD_RATIO = 63f / 88f

@Composable
fun CardImage(card: HasImage, label: String, modifier: Modifier = Modifier, detail: Boolean = false) {
    val images = LocalContext.current.container.images
    val saved by images.saved.collectAsStateWithLifecycle()
    // Re-picks the model when the card's offline image appears or is deleted.
    val isSaved = card.id in saved
    val model = remember(card.id, detail, isSaved) { if (detail) images.detailModel(card) else images.thumbModel(card) }
    Box(
        modifier
            .aspectRatio(CARD_RATIO)
            .clip(RoundedCornerShape(if (detail) 12.dp else 6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        // Shown under the image while it loads, and on its own when there's no image.
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(6.dp),
        )
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun RarityLabel(rarity: String?, parallel: Boolean, modifier: Modifier = Modifier) {
    val text = buildString {
        append(rarity ?: "?")
        if (parallel) append(" ★")
    }
    Text(
        text,
        modifier = modifier,
        color = rarityColor(rarity, parallel),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelSmall,
    )
}

@Composable
fun Badge(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Surface(color = color, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            text,
            color = MaterialTheme.colorScheme.surface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
        )
    }
}

@Composable
fun CardTile(card: CardRow, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(onClick = onClick).padding(4.dp)) {
        Box {
            CardImage(card, label = "${card.code}\n${card.displayName}", modifier = Modifier.fillMaxWidth())
            Row(
                Modifier.align(Alignment.TopEnd).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (card.wished) {
                    Icon(Icons.Filled.Favorite, "Wishlisted", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                }
                if (card.owned > 0) Badge("×${card.owned}")
            }
            if (card.status == "announced") {
                Badge("Announced", Modifier.align(Alignment.BottomStart).padding(4.dp), MaterialTheme.colorScheme.secondary)
            }
        }
        Row(Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (card.kind == "don") card.id else card.code,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            RarityLabel(card.rarity, card.variant != null)
        }
        Text(card.displayName, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun CardGrid(
    cards: List<CardRow>,
    onCard: (String) -> Unit,
    modifier: Modifier = Modifier,
    header: (LazyGridScope.() -> Unit)? = null,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(108.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
    ) {
        header?.invoke(this)
        items(cards, key = { it.id }) { card -> CardTile(card, onClick = { onCard(card.id) }) }
    }
}

@Composable
fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Sections on the Sets screen, in display order. */
val SET_SECTIONS = listOf(
    "booster" to "Booster packs",
    "extra" to "Extra boosters",
    "premium" to "Premium boosters",
    "starter" to "Starter decks",
    "don" to "DON!! cards",
    "promo" to "Promotional cards",
    "limited" to "Limited products",
    "family" to "Family decks",
    "announced" to "Announced, not yet listed",
)
