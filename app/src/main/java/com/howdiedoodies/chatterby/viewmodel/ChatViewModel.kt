package com.howdiedoodies.chatterby.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howdiedoodies.chatterby.data.ChatService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(val user: String, val text: String)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val currentMessage: String = ""
)

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val chatService = ChatService()

    init {
        chatService.messages
            .onEach { messages ->
                _uiState.update {
                    it.copy(messages = messages.map {
                        val parts = it.split(":")
                        ChatMessage(parts.first(), parts.last())
                    })
                }
            }
            .launchIn(viewModelScope)
    }

    fun connect(url: String, authMessage: String) {
        chatService.connect(url, authMessage)
    }

    fun onMessageChanged(message: String) {
        _uiState.update { it.copy(currentMessage = message) }
    }

    fun sendMessage() {
        if (_uiState.value.currentMessage.isBlank()) return

        chatService.sendMessage(_uiState.value.currentMessage)
        _uiState.update {
            it.copy(currentMessage = "")
        }
    }
}
