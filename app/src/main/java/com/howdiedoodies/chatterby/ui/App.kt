package com.howdiedoodies.chatterby.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.howdiedoodies.chatterby.R
import com.howdiedoodies.chatterby.ui.SettingsScreen

@Composable
fun App() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(
                    Screen.Favorites,
                    Screen.Search
                )
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(id = screen.icon), contentDescription = null) },
                        label = { Text(screen.route) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        val favoriteViewModel: com.howdiedoodies.chatterby.viewmodel.FavoriteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        NavHost(navController, startDestination = Screen.Favorites.route, Modifier.padding(innerPadding)) {
            composable(Screen.Favorites.route) { FavoriteScreen(navController, favoriteViewModel) }
            composable(Screen.Search.route) { SearchScreen(navController, favoriteViewModel = favoriteViewModel) }
            composable("room/{username}") { backStackEntry ->
                RoomScreen(navController, backStackEntry.arguments?.getString("username") ?: "")
            }
            composable("login") { LoginScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
        }
    }
}

sealed class Screen(val route: String, val icon: Int) {
    object Favorites : Screen("Favorites", R.drawable.ic_favorite)
    object Search : Screen("Search", R.drawable.ic_search)
}
