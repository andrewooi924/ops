package com.optcg.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.container
import com.optcg.app.data.CardRow

/**
 * The original Prices tab: a wishlist grouped into rows (Leader, SP, ...) with each card's
 * average price underneath. Cards come from the wishlist (the heart on a card's page).
 */
@Composable
fun PricesScreen(nav: Nav) {
    val container = LocalContext.current.container
    val wishlist by remember { container.db.catalog().wishlistCards() }.collectAsStateWithLifecycle(emptyList())
    val groups = priceGroups(wishlist)

    Column(Modifier.fillMaxSize().background(Palette.Background).statusBarsPadding()) {
        Text("Wishlist", fontSize = 26.sp, color = Palette.Text, modifier = Modifier.padding(start = 14.dp, top = 16.dp, bottom = 4.dp))
        if (wishlist.isEmpty()) {
            EmptyState("Tap the heart on any card to add it here and follow its price.")
            return@Column
        }
        LazyColumn(Modifier.fillMaxSize()) {
            groups.forEach { (title, cards) ->
                item(key = "title-$title") {
                    Text(title, fontSize = 22.sp, color = Palette.Text, modifier = Modifier.padding(start = 14.dp, top = 16.dp, bottom = 8.dp))
                }
                item(key = "row-$title") {
                    LazyRow(contentPadding = PaddingValues(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(cards, key = { it.id }) { card -> PriceTile(card) { nav.card(card.id) } }
                    }
                }
            }
        }
    }
}

@Composable
fun PriceTile(card: CardRow, onClick: () -> Unit) {
    Column(Modifier.width(140.dp).clickable(onClick = onClick)) {
        CardImage(card, label = "${card.code}\n${card.displayName}", modifier = Modifier.fillMaxWidth())
        // Prices aren't collected yet; they'll come from the daily catalog run.
        Text("Avg: —", color = Palette.SubText, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        Text(card.displayName, color = Palette.SubText, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private fun priceGroups(cards: List<CardRow>): List<Pair<String, List<CardRow>>> {
    fun group(card: CardRow) = when {
        card.kind == "don" -> "DON!!"
        card.rarity == "L" -> "Leader"
        card.rarity == "SP" || card.rarity == "TR" -> "SP"
        card.rarity == "P" -> "Promo"
        card.rarity == "SEC" -> "SEC"
        else -> "Other"
    }
    val order = listOf("Leader", "SP", "SEC", "Promo", "Other", "DON!!")
    return cards.groupBy(::group).toList().sortedBy { order.indexOf(it.first) }
}

/** The original Portfolio tab: collection value with a chart, then the price trend of owned cards. */
@Composable
fun PortfolioScreen(nav: Nav) {
    val container = LocalContext.current.container
    val owned by remember { container.db.catalog().ownedCards() }.collectAsStateWithLifecycle(emptyList())
    Column(Modifier.fillMaxSize().background(Palette.Background).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, top = 16.dp), verticalAlignment = Alignment.Top) {
            Text("Portfolio", fontSize = 26.sp, color = Palette.Text, modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("MYR —", fontSize = 22.sp, color = Palette.Text)
                Text("${owned.size} cards · ${owned.sumOf { it.owned }} copies", fontSize = 11.sp, color = Palette.SubText)
            }
        }
        Box(
            Modifier
                .padding(14.dp)
                .fillMaxWidth()
                .height(180.dp)
                .background(Palette.Surface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text("Value history appears once prices are available.", color = Palette.SubText, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
        Text("Price Trend", fontSize = 22.sp, color = Palette.Text, modifier = Modifier.padding(start = 14.dp, top = 8.dp, bottom = 8.dp))
        if (owned.isEmpty()) {
            EmptyState("Cards you own show up here.")
        } else {
            LazyRow(contentPadding = PaddingValues(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(owned, key = { it.id }) { card -> PriceTile(card) { nav.card(card.id) } }
            }
        }
    }
}
