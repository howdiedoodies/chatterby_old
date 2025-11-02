package com.howdiedoodies.chatterby.data

import com.ditchoom.websocket.WebSocketClient
import com.ditchoom.websocket.WebSocketConnectionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var webSocket: WebSocketClient? = null

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    fun connect(url: String, authMessage: String) {
        scope.launch {
            try {
                val connectionOptions = WebSocketConnectionOptions(
                    name = url,
                    port = 443,
                    websocketEndpoint = "/",
                    tls = true
                )
                webSocket = WebSocketClient.allocate(connectionOptions)
                webSocket?.connect()
                webSocket?.write(authMessage)

                while (true) {
                    val message = webSocket?.read()
                    if (message is com.ditchoom.buffer.DataRead.StringDataRead) {
                        _messages.value = _messages.value + message.string
                    }
                }
            } catch (e: Exception) {
                // Handle connection errors
            }
        }
    }

    fun disconnect() {
        scope.launch {
            webSocket?.close()
        }
    }

    fun sendMessage(message: String) {
        scope.launch {
            webSocket?.write(message)
        }
    }
}
