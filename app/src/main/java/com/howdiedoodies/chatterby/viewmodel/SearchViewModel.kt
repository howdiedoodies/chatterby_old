package com.howdiedoodies.chatterby.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howdiedoodies.chatterby.data.Cam
import com.howdiedoodies.chatterby.data.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<Cam> = emptyList()
)

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun search() {
        viewModelScope.launch {
            if (_uiState.value.query.isNotBlank()) {
                try {
                    val searchResult = NetworkModule.api.search(query = _uiState.value.query, usernames = null)
                    _uiState.value = _uiState.value.copy(results = searchResult.results)
                } catch (e: Exception) {
                    // Handle network errors
                }
            }
        }
    }
}
