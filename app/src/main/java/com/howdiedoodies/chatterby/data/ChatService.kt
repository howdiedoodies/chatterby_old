package com.howdiedoodies.chatterby.data

import com.ditchoom.buffer.toBuffer
import com.ditchoom.socket.NetworkCapabilities
import com.ditchoom.websocket.WebSocketConnectionOptions
import com.ditchoom.websocket.client.WebSocketClient
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
            if (!NetworkCapabilities.isNetworkAvailable()) return@launch
            try {
                val uri = URI(url)
                val connectionOptions = WebSocketConnectionOptions(
                    hostname = uri.host,
                    port = uri.port,
                    websocketEndpoint = uri.path,
                    tls = uri.scheme == "wss"
                )
                webSocket = WebSocketClient(connectionOptions)
                webSocket?.write(authMessage.toBuffer())

                while (true) {
                    val message = webSocket?.read()?.readUtf8()
                    message?.let {
                        _messages.value = _messages.value + it
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
            webSocket?.write(message.toBuffer())
        }
    }
}
