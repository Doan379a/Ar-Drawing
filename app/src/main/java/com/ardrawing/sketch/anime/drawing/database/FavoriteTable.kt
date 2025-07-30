package com.ardrawing.sketch.anime.drawing.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_items")
data class FavoriteTable(
   @PrimaryKey val imgResId: Int
)
