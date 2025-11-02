package com.howdiedoodies.chatterby.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.howdiedoodies.chatterby.data.AppDatabase
import com.howdiedoodies.chatterby.data.Favorite
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val favoriteDao = AppDatabase.getDatabase(application).favoriteDao()
    val favorites: StateFlow<List<Favorite>> = favoriteDao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _isRefreshing = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun addFavorite(username: String) {
        viewModelScope.launch {
            favoriteDao.insert(Favorite(username = username))
        }
    }

    fun removeFavorite(favorite: Favorite) {
        viewModelScope.launch {
            favoriteDao.delete(favorite)
        }
    }

    fun refreshFavorites() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val usernames = favorites.value.joinToString(",") { it.username }
                val searchResult = com.howdiedoodies.chatterby.data.NetworkModule.api.search("", usernames)
                searchResult.results.forEach { cam ->
                    val isOnline = cam.roomStatus == "public"
                    favoriteDao.updateStatus(cam.username, isOnline, System.currentTimeMillis())
                }
            } catch (e: Exception) {
                // Handle network errors
            }
            _isRefreshing.value = false
        }
    }
}