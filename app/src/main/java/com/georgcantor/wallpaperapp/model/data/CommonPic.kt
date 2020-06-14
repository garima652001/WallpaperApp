package com.georgcantor.wallpaperapp.model.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommonPic(
        val url: String,
        val width: Int?,
        val height: Int?,
        var tag: String?,
        var imageUrl: String?,
        var fullHdUrl: String?,
        var id: Int?
) : Parcelable