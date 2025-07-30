package com.ardrawing.sketch.anime.drawing.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query



@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteTable): Long

    @Delete
    suspend fun delete(item: FavoriteTable)

    @Query("SELECT * FROM favorite_items")
    fun getAllFavorites(): LiveData<List<FavoriteTable>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE imgResId = :imgResId)")
    suspend fun isFavorite(imgResId: Int): Boolean
}



