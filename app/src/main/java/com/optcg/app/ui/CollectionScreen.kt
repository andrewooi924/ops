package com.optcg.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.R
import com.optcg.app.container

/** The original Collection tab: a grid of set banners, with search and filter at the top. */
@Composable
fun CollectionScreen(nav: Nav) {
    val container = LocalContext.current.container
    val sets by remember { container.db.catalog().setProgress() }.collectAsStateWithLifecycle(emptyList())
    var kind by rememberSaveable { mutableStateOf<String?>(null) }
    var menu by remember { mutableStateOf(false) }
    val known = SET_SECTIONS.map { it.first }
    val shown = sets.filter { kind == null || it.kind == kind }
        .sortedBy { known.indexOf(it.kind).let { i -> if (i < 0) known.size else i } }

    Column(Modifier.fillMaxSize().background(Palette.Background).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                kind?.let { k -> SET_SECTIONS.firstOrNull { it.first == k }?.second } ?: "Collection",
                fontSize = 22.sp,
                color = Palette.Text,
                modifier = Modifier.weight(1f),
            )
            Image(painterResource(R.drawable.icon_search), "Search", Modifier.size(40.dp).clickable(onClick = nav::search).padding(8.dp))
            Box {
                Image(painterResource(R.drawable.icon_filter), "Filter", Modifier.size(40.dp).clickable { menu = true }.padding(8.dp))
                DropdownMenu(menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("All sets") }, onClick = { kind = null; menu = false })
                    SET_SECTIONS.filter { (k, _) -> sets.any { it.kind == k } }.forEach { (k, title) ->
                        DropdownMenuItem(text = { Text(title) }, onClick = { kind = k; menu = false })
                    }
                }
            }
        }
        LazyVerticalGrid(
            GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(shown, key = { it.id }) { set ->
                Column(Modifier.padding(bottom = 12.dp).clickable { nav.set(set.id) }) {
                    SetBanner(set.code, setDisplayName(set), Modifier.fillMaxWidth())
                    Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            set.code ?: setDisplayName(set),
                            fontSize = 12.sp,
                            color = Palette.SubText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text("${set.owned}/${set.total}", fontSize = 12.sp, color = if (set.owned > 0) Palette.Selected else Palette.SubText)
                    }
                }
            }
            if (shown.isEmpty()) item(span = { GridItemSpan(2) }) { EmptyState("Loading sets…") }
        }
    }
}
