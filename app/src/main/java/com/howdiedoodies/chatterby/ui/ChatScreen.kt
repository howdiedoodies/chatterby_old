package com.howdiedoodies.chatterby.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.howdiedoodies.chatterby.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, viewModel: ChatViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* TODO: Emoji picker */ }) {
                    Icon(Icons.Default.Face, contentDescription = "Emoji")
                }
                TextField(
                    value = uiState.currentMessage,
                    onValueChange = { viewModel.onMessageChanged(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") }
                )
                IconButton(onClick = { /* TODO: Send GIF */ }) {
                    Icon(Icons.Default.Add, contentDescription = "GIF")
                }
                IconButton(onClick = { viewModel.sendMessage() }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(uiState.messages) { message ->
                Text("${message.user}: ${message.text}")
            }
        }
    }
}
