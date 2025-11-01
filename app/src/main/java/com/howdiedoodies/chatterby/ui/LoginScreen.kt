package com.howdiedoodies.chatterby.ui

import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.howdiedoodies.chatterby.data.AppDatabase
import com.howdiedoodies.chatterby.data.Favorite
import com.howdiedoodies.chatterby.data.FavoriteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WebAppInterface(
    private val favoriteDao: FavoriteDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @JavascriptInterface
    fun importFavorites(usernames: String) {
        scope.launch {
            usernames.split(",")
                .filter { it.isNotBlank() }
                .forEach {
                    favoriteDao.insert(Favorite(username = it))
                }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    AndroidView(factory = {
        val favoriteDao = AppDatabase.getDatabase(it).favoriteDao()
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            addJavascriptInterface(WebAppInterface(favoriteDao), "Android")
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (url == "https://chaturbate.com/") {
                        view?.loadUrl("https://chaturbate.com/followed-cams/")
                    } else if (url == "https://chaturbate.com/followed-cams/") {
                        view?.loadUrl("javascript:Android.importFavorites(Array.from(document.querySelectorAll('a[data-room]')).map(a => a.getAttribute('data-room')).join(','));")
                    }
                }
            }
            loadUrl("https://chaturbate.com/auth/login/")
        }
    }, update = {
        it.loadUrl("https://chaturbate.com/auth/login/")
    })
}
