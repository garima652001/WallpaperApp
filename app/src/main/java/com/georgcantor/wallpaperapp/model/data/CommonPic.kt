package com.georgcantor.wallpaperapp.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommonPic(
        val url: String,
        val width: Int?,
        val height: Int?,
        var favorites: Int?,
        var tag: String?,
        var downloads: Int?,
        var imageUrl: String?,
        var fullHdUrl: String?,
        var user: String?,
        var id: Int?,
        var userImageUrl: String?
) : Parcelable