package com.georgcantor.wallpaperapp.ui.fragment.pictures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.ApiRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class PicturesViewModel(
    app: Application,
    private val apiRepository: ApiRepository
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()

    val pictures = MutableLiveData<MutableList<CommonPic>>()
    val error = MutableLiveData<String>()

    fun getPictures(query: String, index: Int) {
        Observable.fromCallable {
                apiRepository.getAbyssPictures(query, index)
                    .subscribe(pictures::postValue) {
                        error.postValue(it.message)
                    }
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}