package com.optcg.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val TABS = listOf(
    Tab("sets", "Sets", Icons.AutoMirrored.Filled.List),
    Tab("search", "Search", Icons.Filled.Search),
    Tab("collection", "Collection", Icons.Filled.Favorite),
    Tab("settings", "Settings", Icons.Filled.Settings),
)

class Nav(private val controller: NavHostController) {
    fun set(id: String) = controller.navigate("set/$id")
    fun card(id: String) = controller.navigate("card/$id")
    fun sim(setId: String) = controller.navigate("sim/$setId")
    fun back() { controller.popBackStack() }
}

@Composable
fun AppRoot() {
    val controller = rememberNavController()
    val nav = Nav(controller)
    val entry by controller.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    Scaffold(
        bottomBar = {
            if (TABS.any { it.route == route }) {
                NavigationBar {
                    TABS.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = {
                                controller.navigate(tab.route) {
                                    popUpTo(controller.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, null) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(controller, startDestination = "sets", modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
            composable("sets") { SetsScreen(nav) }
            composable("search") { SearchScreen(nav) }
            composable("collection") { CollectionScreen(nav) }
            composable("settings") { SettingsScreen() }
            composable("set/{id}") { SetScreen(it.arguments?.getString("id").orEmpty(), nav) }
            composable("card/{id}") { CardScreen(it.arguments?.getString("id").orEmpty(), nav) }
            composable("sim/{id}") { SimScreen(it.arguments?.getString("id").orEmpty(), nav) }
        }
    }
}
