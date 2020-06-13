package com.georgcantor.wallpaperapp.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.georgcantor.wallpaperapp.model.local.Favorite.Favorite.tableName

@Entity(tableName = tableName)
data class Favorite(
    @PrimaryKey
    var url: String,
    @ColumnInfo(name = Favorite.Column.image)
    var image: String?
) {

    object Favorite {
        const val tableName = "favorites"

        object Column {
            const val url = "url"
            const val image = "image"
        }
    }
}
