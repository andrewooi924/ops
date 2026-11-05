package com.optcg.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.container

@Composable
fun CollectionScreen(nav: Nav) {
    val container = LocalContext.current.container
    val owned by remember { container.db.catalog().ownedCards() }.collectAsStateWithLifecycle(emptyList())
    val wishlist by remember { container.db.catalog().wishlistCards() }.collectAsStateWithLifecycle(emptyList())
    var tab by rememberSaveable { mutableIntStateOf(0) }

    Column(Modifier.statusBarsPadding().padding(top = 8.dp)) {
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Owned (${owned.size})") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Wishlist (${wishlist.size})") })
        }
        if (tab == 0) {
            if (owned.isEmpty()) {
                EmptyState("Cards you own show up here. Add them from a card's page, or from packs you open in the simulator.")
            } else {
                Text(
                    "${owned.size} different cards · ${owned.sumOf { it.owned }} copies",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                CardGrid(owned, onCard = nav::card)
            }
        } else {
            if (wishlist.isEmpty()) {
                EmptyState("Tap the heart on a card to add it to your wishlist.")
            } else {
                CardGrid(wishlist, onCard = nav::card)
            }
        }
    }
}
