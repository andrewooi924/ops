package com.optcg.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.optcg.app.container
import com.optcg.app.data.CardRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Composable
fun SearchScreen(nav: Nav) {
    val dao = LocalContext.current.container.db.catalog()
    var query by rememberSaveable { mutableStateOf("") }
    val results by remember {
        snapshotFlow { query.trim() }
            .debounce(200)
            .flatMapLatest { text ->
                if (text.length < 2) flowOf(emptyList<CardRow>()) else dao.search("%${escapeLike(text)}%")
            }
    }.collectAsStateWithLifecycle(emptyList())

    Column(Modifier.statusBarsPadding().padding(top = 8.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Name, code or type (English or 日本語)") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            singleLine = true,
        )
        when {
            query.trim().length < 2 -> EmptyState("Search every card in the catalog, including parallels and DON!! cards.")
            results.isEmpty() -> EmptyState("No cards match \"${query.trim()}\".")
            else -> CardGrid(results, onCard = nav::card)
        }
    }
}

/** Drops % so it can't act as a LIKE wildcard; a stray _ only matches one character, which is harmless. */
private fun escapeLike(text: String) = text.replace("%", "")
