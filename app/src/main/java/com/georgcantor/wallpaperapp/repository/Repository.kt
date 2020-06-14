package com.georgcantor.wallpaperapp.repository

import com.georgcantor.wallpaperapp.BuildConfig.UNSPLASH_URL
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.data.pixabay.Picture
import com.georgcantor.wallpaperapp.model.local.FavDao
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.model.remote.ApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class Repository(
    private val apiService: ApiService,
    private val dao: FavDao
) {

    suspend fun getPixabayPictures(query: String, index: Int): MutableList<CommonPic> {
        val commonPics = mutableListOf<CommonPic>()
        apiService.getPixabayPictures(query, index).apply {
            pictures.map {
                if (it.id != 158703 && it.id != 158704) {
                    commonPics.add(
                        CommonPic(
                            it.webformatUrl ?: "",
                            it.imageWidth,
                            it.imageHeight,
                            it.tags?.split(",")?.get(0) ?: "",
                            it.imageUrl,
                            it.fullHdUrl,
                            it.id
                        )
                    )
                }
            }
        }
        commonPics.shuffle()

        return commonPics
    }

    suspend fun getUnsplashPictures(query: String, index: Int): MutableList<CommonPic> {
        val commonPics = mutableListOf<CommonPic>()
        apiService.getUnsplashPictures(UNSPLASH_URL, query, index).apply {
            results?.map {
                commonPics.add(
                    CommonPic(
                        it.urls?.small ?: "",
                        it.width ?: 0,
                        it.height ?: 0,
                        "",
                        it.urls?.full,
                        it.urls?.regular,
                        it.hashCode()
                    )
                )
            }
        }
        commonPics.shuffle()

        return commonPics
    }

    suspend fun getOnePicture(query: String): Picture {
        val response = apiService.getPixabayPictures(query, 1)

        return response.pictures[0]
    }

    suspend fun insertFavoriteAsync(favorite: Favorite) = coroutineScope {
        async { dao.insert(favorite) }
    }

    suspend fun deleteByUrlAsync(url: String) = coroutineScope {
        async { dao.deleteByUrl(url) }
    }

    suspend fun deleteAllAsync() = coroutineScope {
        async { dao.deleteAll() }
    }

    suspend fun getByUrlAsync(url: String) = coroutineScope {
        async { dao.getByUrl(url) }
    }

    suspend fun getAllAsync() = coroutineScope {
        async { dao.getAll() }
    }
}