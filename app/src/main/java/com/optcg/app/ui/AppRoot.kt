package com.optcg.app.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.optcg.app.R

private data class Tab(val route: String, val label: String, @DrawableRes val icon: Int?, @DrawableRes val selectedIcon: Int?)

// The original bottom bar: Home, Shop, Collection, Prices, Portfolio.
private val TABS = listOf(
    Tab("home", "Home", R.drawable.icon_home, R.drawable.icon_home_slt),
    Tab("shop", "Shop", R.drawable.icon_shop, R.drawable.icon_shop_slt),
    Tab("collection", "Collection", R.drawable.icon_collection, R.drawable.icon_collection_slt),
    Tab("prices", "Prices", R.drawable.icon_price, R.drawable.icon_price_slt),
    // The portfolio icon was never committed to the repository, so it's drawn instead.
    Tab("portfolio", "Portfolio", null, null),
)

class Nav(private val controller: NavHostController) {
    fun set(id: String) = controller.navigate("set/$id")
    fun card(id: String) = controller.navigate("card/$id")
    fun sim(setId: String) = controller.navigate("sim/$setId")
    fun search() = controller.navigate("search")
    fun settings() = controller.navigate("settings")
    fun back() { controller.popBackStack() }
}

@Composable
fun AppRoot() {
    val controller = rememberNavController()
    val nav = Nav(controller)
    val entry by controller.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    Column(Modifier.fillMaxSize().background(Palette.Background)) {
        NavHost(controller, startDestination = "home", modifier = Modifier.weight(1f)) {
            composable("home") { HomeScreen(nav) }
            composable("shop") { ShopScreen() }
            composable("collection") { CollectionScreen(nav) }
            composable("prices") { PricesScreen(nav) }
            composable("portfolio") { PortfolioScreen(nav) }
            composable("search") { SearchScreen(nav) }
            composable("settings") { SettingsScreen(nav) }
            composable("set/{id}") { SetScreen(it.arguments?.getString("id").orEmpty(), nav) }
            composable("card/{id}") { CardScreen(it.arguments?.getString("id").orEmpty(), nav) }
            composable("sim/{id}") { SimScreen(it.arguments?.getString("id").orEmpty(), nav) }
        }
        // Like the original, the bar is hidden while the simulator is open.
        if (route?.startsWith("sim/") != true) {
            BottomBar(route) { tab ->
                controller.navigate(tab) {
                    popUpTo(controller.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}

@Composable
private fun BottomBar(route: String?, onTab: (String) -> Unit) {
    val current = TABS.firstOrNull { it.route == route }?.route
        ?: when {
            route == null -> "home"
            route.startsWith("set/") || route == "search" -> "collection"
            else -> null
        }
    Row(
        Modifier
            .fillMaxWidth()
            .background(Palette.NavBar)
            .navigationBarsPadding()
            .height(70.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TABS.forEach { tab ->
            val selected = tab.route == current
            Column(
                Modifier
                    .weight(1f)
                    .clickable { onTab(tab.route) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val icon = if (selected) tab.selectedIcon else tab.icon
                if (icon != null) {
                    Image(painterResource(icon), null, Modifier.size(22.dp))
                } else {
                    PortfolioIcon(if (selected) Palette.Selected else Palette.Unselected)
                }
                Text(
                    tab.label,
                    color = if (selected) Palette.Selected else Palette.Unselected,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun PortfolioIcon(color: Color) {
    Canvas(Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.08f
        drawRoundRect(color, style = Stroke(stroke), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.15f))
        val line = Path().apply {
            moveTo(w * 0.2f, h * 0.7f); lineTo(w * 0.42f, h * 0.45f); lineTo(w * 0.6f, h * 0.58f); lineTo(w * 0.82f, h * 0.28f)
        }
        drawPath(line, color, style = Stroke(stroke, cap = StrokeCap.Round))
        drawCircle(color, radius = stroke, center = Offset(w * 0.82f, h * 0.28f))
    }
}
