package com.howdiedoodies.chatterby.viewmodel

import android.app.Application
import android.content.ContentValues
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.howdiedoodies.chatterby.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteDao = AppDatabase.getDatabase(application).favoriteDao()

    private val _exportResult = MutableStateFlow<String?>(null)
    val exportResult = _exportResult.asStateFlow()

    fun exportFavorites() {
        viewModelScope.launch {
            try {
                val favorites = favoriteDao.getAllFavorites()
                val content = favorites.joinToString("\n") { it.username }

                val resolver = getApplication<Application>().contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "favorites.txt")
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/")
                }

                val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                uri?.let {
                    resolver.openOutputStream(it).use { outputStream ->
                        outputStream?.write(content.toByteArray())
                    }
                }
                _exportResult.value = "Favorites exported to Downloads folder"
            } catch (e: Exception) {
                _exportResult.value = "Error exporting favorites"
            }
        }
    }

    fun onExportResultShown() {
        _exportResult.value = null
    }
}
