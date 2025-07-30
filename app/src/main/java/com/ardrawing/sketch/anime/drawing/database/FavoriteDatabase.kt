package com.ardrawing.sketch.anime.drawing.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context


@Database(entities = [FavoriteTable::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteItemDao(): FavoriteDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { instance = it }
            }
    }
}

