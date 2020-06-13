package com.georgcantor.wallpaperapp.model.data.pixabay

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Picture(
    @SerializedName("previewHeight")
    @Expose
    var previewHeight: Int = 0,
    @SerializedName("likes")
    @Expose
    var likes: Int = 0,
    @SerializedName("favorites")
    @Expose
    var favorites: Int = 0,
    @SerializedName("tags")
    @Expose
    var tags: String?,
    @SerializedName("webformatHeight")
    @Expose
    var webformatHeight: Int = 0,
    @SerializedName("views")
    @Expose
    var views: Int = 0,
    @SerializedName("webformatWidth")
    @Expose
    var webformatWidth: Int = 0,
    @SerializedName("previewWidth")
    @Expose
    var previewWidth: Int = 0,
    @SerializedName("comments")
    @Expose
    var comments: Int = 0,
    @SerializedName("downloads")
    @Expose
    var downloads: Int = 0,
    @SerializedName("pageURL")
    @Expose
    var pageUrl: String?,
    @SerializedName("previewURL")
    @Expose
    var previewUrl: String?,
    @SerializedName("webformatURL")
    @Expose
    var webformatUrl: String?,
    @SerializedName("imageURL")
    @Expose
    var imageUrl: String?,
    @SerializedName("fullHDURL")
    @Expose
    var fullHdUrl: String?,
    @SerializedName("imageWidth")
    @Expose
    var imageWidth: Int = 0,
    @SerializedName("user_id")
    @Expose
    var userId: Int = 0,
    @SerializedName("user")
    @Expose
    var user: String?,
    @SerializedName("type")
    @Expose
    var type: String?,
    @SerializedName("id")
    @Expose
    var id: Int = 0,
    @SerializedName("userImageURL")
    @Expose
    var userImageUrl: String?,
    @SerializedName("imageHeight")
    @Expose
    var imageHeight: Int = 0
) : Parcelable