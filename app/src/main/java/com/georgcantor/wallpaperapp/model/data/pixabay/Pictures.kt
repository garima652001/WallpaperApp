package com.georgcantor.wallpaperapp.model.data.pixabay

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pictures(
    @SerializedName("hits")
    @Expose
    var pictures: List<Picture>
) : Parcelable