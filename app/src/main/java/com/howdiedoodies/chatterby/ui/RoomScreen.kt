package com.howdiedoodies.chatterby.ui

import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.howdiedoodies.chatterby.viewmodel.ChatViewModel
import timber.log.Timber

class RoomWebAppInterface(
    private val viewModel: ChatViewModel
) {
    @JavascriptInterface
    fun onConnectionDetailsExtracted(url: String, authMessage: String) {
        Timber.d("WebSocket URL: %s", url)
        Timber.d("Auth Message: %s", authMessage)
        viewModel.connect(url, authMessage)
    }
}

@Composable
fun RoomScreen(navController: NavController, username: String) {
    val chatViewModel: ChatViewModel = viewModel()
    Column {
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                addJavascriptInterface(RoomWebAppInterface(chatViewModel), "Android")
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view?.loadUrl(
                            """
                            javascript:(function() {
                                const url = window.performance.getEntries().find(e => e.name.includes('sock-')).name;
                                const authMessage = window.performance.getEntries().find(e => e.name.includes('{"method":"connect"')).name;
                                Android.onConnectionDetailsExtracted(url, authMessage);
                            })()
                            """
                        )
                    }
                }
                loadUrl("https://chaturbate.com/$username")
            }
        }, modifier = Modifier.weight(1f))
        ChatScreen(navController = navController, viewModel = chatViewModel)
    }
}
