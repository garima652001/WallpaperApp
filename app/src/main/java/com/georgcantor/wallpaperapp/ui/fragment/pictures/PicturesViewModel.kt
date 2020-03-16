package com.georgcantor.wallpaperapp.ui.fragment.pictures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.ApiRepository
import com.georgcantor.wallpaperapp.util.Constants.Companion.AUDI
import com.georgcantor.wallpaperapp.util.Constants.Companion.BMW
import com.georgcantor.wallpaperapp.util.Constants.Companion.MERCEDES
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class PicturesViewModel(
    app: Application,
    private val apiRepository: ApiRepository
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()

    val pictures = MutableLiveData<MutableList<CommonPic>>()
    val error = MutableLiveData<String>()

    fun getPictures(index: Int, query: String) {
        Observable.fromCallable {
                apiRepository.getAbyssPictures(checkIsCarQuery(index, query), index)
                    .subscribe(pictures::postValue) {
                        error.postValue(it.message)
                    }
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun checkIsCarQuery(index: Int, query: String): String {
        when (query) {
            BMW -> {
                return try {
                    val requests = arrayListOf("BMW m8", "BMW m5", "BMW x6", "BMW x5", "BMW x7", "BMW 7", "BMW concept", "BMW 5", "BMW m4", "BMW m3", "BMW m2", "BMW 3", "BMW x4", "BMW 6", "BMW M", "BMW x3")
                    requests[index - 1]
                } catch (e: IndexOutOfBoundsException) {
                    context.getString(R.string.bmw_request)
                }
            }
            AUDI -> {
                return try {
                    val requests = arrayListOf("Audi q8", "Audi s8", "Audi r8", "Audi s5", "Audi concept", "Audi 7", "Audi tt", "Audi a4")
                    requests[index - 1]
                } catch (e: IndexOutOfBoundsException) {
                    context.getString(R.string.audi_request)
                }
            }
            MERCEDES -> {
                return try {
                    val requests = arrayListOf("mercedes-benz amg", "mercedes-benz s", "mercedes-benz g", "mercedes-benz e", "mercedes-benz c", "mercedes-benz concept", "mercedes-benz ml", "mercedes-benz gl", "mercedes-benz gt")
                    requests[index - 1]
                } catch (e: IndexOutOfBoundsException) {
                    context.getString(R.string.mercedes_request)
                }
            }
        }

        return query;
    }
}