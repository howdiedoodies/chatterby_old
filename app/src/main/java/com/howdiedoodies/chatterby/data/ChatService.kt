package com.howdiedoodies.chatterby.data

import com.ditchoom.websocket.DataRead
import com.ditchoom.websocket.WebSocketClient
import com.ditchoom.websocket.WebSocketConnectionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URI

class ChatService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var webSocket: WebSocketClient? = null

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    fun connect(url: String, authMessage: String) {
        scope.launch {
            try {
                val uri = URI(url)
                val connectionOptions = WebSocketConnectionOptions(
                    name = uri.host,
                    port = uri.port,
                    websocketEndpoint = uri.path,
                    tls = uri.scheme == "wss"
                )
                webSocket = WebSocketClient.Companion.allocate(connectionOptions)
                webSocket?.connect()
                webSocket?.write(authMessage)

                while (true) {
                    val message = webSocket?.read()
                    if (message is DataRead.StringDataRead) {
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
