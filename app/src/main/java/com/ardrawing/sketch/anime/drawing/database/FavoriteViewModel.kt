package com.ardrawing.sketch.anime.drawing.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class FavoriteViewModel(private val db: AppDatabase) : ViewModel() {

    val favorites: LiveData<List<FavoriteTable>> = db.favoriteItemDao().getAllFavorites()


    fun addFavorite(item: FavoriteTable) {
        viewModelScope.launch {
            db.favoriteItemDao().insert(item)
        }
    }

    fun deleteFavorite(item: FavoriteTable) {
        viewModelScope.launch {
            db.favoriteItemDao().delete(item)
        }
    }

    suspend fun isFavorite(imgResId: Int): Boolean {
        return db.favoriteItemDao().isFavorite(imgResId)
    }
}


class FavoriteViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
