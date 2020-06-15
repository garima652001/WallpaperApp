package com.georgcantor.wallpaperapp.view.fragment.pictures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.Repository
import com.georgcantor.wallpaperapp.util.Constants.COUNTER
import com.georgcantor.wallpaperapp.util.Constants.ERROR_504
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class PicturesViewModel(
    app: Application,
    private val repository: Repository,
    private val preferenceManager: PreferenceManager
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()

    val isNetworkAvailable = MutableLiveData<Boolean>()
    val isProgressVisible = MutableLiveData<Boolean>().apply { this.value = true }
    val isRatingDialogShowTime = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val pictures = MutableLiveData<MutableList<CommonPic>>()

    init {
        viewModelScope.launch {
            when (val counter = preferenceManager.getInt(COUNTER) ?: 0) {
                in 0..8 -> preferenceManager.saveInt(COUNTER, counter + 1)
                9 -> {
                    isRatingDialogShowTime.postValue(true)
                    preferenceManager.saveInt(COUNTER, counter + 1)
                }
            }
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable.message) {
            ERROR_504 -> error.postValue(context.getString(R.string.no_internet))
            else -> error.postValue(throwable.message)
        }
        isProgressVisible.postValue(false)
    }

    fun getPictures(query: String, index: Int) {
        viewModelScope.launch(exceptionHandler) {
            val mixResponse = repository.getPixabayPictures(query, index) + repository.getUnsplashPictures(query, index)
            pictures.postValue(mixResponse.shuffled().toMutableList())
            isProgressVisible.postValue(false)
        }
        isNetworkAvailable.value = context.isNetworkAvailable()
    }
}