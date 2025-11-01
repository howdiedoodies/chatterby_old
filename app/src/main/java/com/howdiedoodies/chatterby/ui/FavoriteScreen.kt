package com.howdiedoodies.chatterby.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.howdiedoodies.chatterby.data.Favorite
import com.howdiedoodies.chatterby.viewmodel.FavoriteViewModel

@Composable
fun FavoriteScreen(navController: NavController, viewModel: FavoriteViewModel) {
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Column {
        Button(onClick = { navController.navigate("login") }) {
            Text("Login to Import Favorites")
        }
        com.google.accompanist.swiperefresh.SwipeRefresh(
        state = com.google.accompanist.swiperefresh.rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.refreshFavorites() }
    ) {
        LazyColumn {
            items(favorites) { favorite ->
                FavoriteItem(favorite) {
                    navController.navigate("room/${favorite.username}")
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(favorite: Favorite, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = favorite.username,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = if (favorite.isOnline) "LIVE" else "Offline",
                color = if (favorite.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
