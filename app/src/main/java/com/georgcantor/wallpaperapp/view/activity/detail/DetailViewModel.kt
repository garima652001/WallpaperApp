package com.georgcantor.wallpaperapp.view.activity.detail

import android.app.Application
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.repository.Repository
import com.georgcantor.wallpaperapp.util.Constants.ERROR_504
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

class DetailViewModel(
    app: Application,
    private val repository: Repository
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()

    val isNetworkAvailable = MutableLiveData<Boolean>()

    val isProgressVisible = MutableLiveData<Boolean>().apply { this.value = true }
    val isWallProgressVisible = MutableLiveData<Boolean>().apply { this.value = true }
    val isWallpaperSet = MutableLiveData<Boolean>()
    val isFavorite = MutableLiveData<Boolean>().apply { postValue(false) }
    val isDownload = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val similarImages = MutableLiveData<MutableList<CommonPic>>()
    val uri = MutableLiveData<Uri>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable.message) {
            ERROR_504 -> error.postValue(context.getString(R.string.no_internet))
            else -> error.postValue(throwable.message)
        }
        isProgressVisible.postValue(false)
    }

    fun getSimilarImages(query: String) {
        viewModelScope.launch(exceptionHandler) {
            val response = repository.getPixabayPictures(query, 1)
            similarImages.postValue(response)
            isProgressVisible.postValue(false)
        }
        isNetworkAvailable.value = context.isNetworkAvailable()
    }

    fun checkIsFavourite(url: String) {
        viewModelScope.launch(exceptionHandler) {
            val favorites = repository.getAllAsync().await()
            favorites.map {
                if (it.url == url) isFavorite.postValue(true)
            }
        }
    }

    fun setFavoriteStatus(picture: CommonPic?) {
        when (isFavorite.value) {
            true -> removeFromFavorites(picture?.url ?: "")
            false -> addToFavorites(picture)
        }
    }

    private fun addToFavorites(picture: CommonPic?) {
        viewModelScope.launch(exceptionHandler) {
            val json = Gson().toJson(picture)
            repository.insertFavoriteAsync(Favorite(picture?.url ?: "", json)).await()
            isFavorite.postValue(true)
        }
    }

    private fun removeFromFavorites(url: String) {
        viewModelScope.launch(exceptionHandler) {
            repository.deleteByUrlAsync(url).await()
            isFavorite.postValue(false)
        }
    }

    suspend fun setImageAsWallpaper(pic: CommonPic?) = withContext(Dispatchers.IO) {
        val bitmap = Glide.with(context)
            .asBitmap()
            .load(pic?.imageUrl)
            .submit()
            .get()

        uri.postValue(getImageUri(bitmap))
    }

    private suspend fun getImageUri(bitmap: Bitmap?): Uri {
        return withContext(Dispatchers.Default) {
            val bytes = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver, bitmap, "Title", null
            )

            Uri.parse(path)
        }
    }

    fun setAsWallpaper(bitmap: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            WallpaperManager.getInstance(context).setBitmap(bitmap)
            isWallpaperSet.postValue(true)
            isWallProgressVisible.postValue(false)
        }
    }
}