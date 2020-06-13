package com.georgcantor.wallpaperapp.view.activity.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.Category
import com.georgcantor.wallpaperapp.repository.Repository
import com.georgcantor.wallpaperapp.util.Constants.ANIMALS
import com.georgcantor.wallpaperapp.util.Constants.BUILDINGS
import com.georgcantor.wallpaperapp.util.Constants.COMPUTER
import com.georgcantor.wallpaperapp.util.Constants.EDUCATION
import com.georgcantor.wallpaperapp.util.Constants.ERROR_504
import com.georgcantor.wallpaperapp.util.Constants.FASHION
import com.georgcantor.wallpaperapp.util.Constants.FEELINGS
import com.georgcantor.wallpaperapp.util.Constants.FOOD
import com.georgcantor.wallpaperapp.util.Constants.HEALTH
import com.georgcantor.wallpaperapp.util.Constants.MUSIC
import com.georgcantor.wallpaperapp.util.Constants.NATURE
import com.georgcantor.wallpaperapp.util.Constants.PEOPLE
import com.georgcantor.wallpaperapp.util.Constants.PLACES
import com.georgcantor.wallpaperapp.util.Constants.SCIENCE
import com.georgcantor.wallpaperapp.util.Constants.SPORTS
import com.georgcantor.wallpaperapp.util.Constants.TEXTURES
import com.georgcantor.wallpaperapp.util.Constants.TRAVEL
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class CategoriesViewModel(
    app: Application,
    private val repository: Repository
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()

    val isNetworkAvailable = MutableLiveData<Boolean>()
    val isProgressVisible = MutableLiveData<Boolean>().apply { this.value = true }
    val error = MutableLiveData<String>()
    val categories = MutableLiveData<MutableList<Category>>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable.message) {
            ERROR_504 -> error.postValue(context.getString(R.string.no_internet))
            else -> error.postValue(throwable.message)
        }
        isProgressVisible.postValue(false)
    }

    fun getCategories() {
        viewModelScope.launch(exceptionHandler) {
            val topics = listOf(
                ANIMALS,
                BUILDINGS,
                NATURE,
                TEXTURES,
                TRAVEL,
                PLACES,
                MUSIC,
                HEALTH,
                FASHION,
                FEELINGS,
                FOOD,
                PEOPLE,
                SCIENCE,
                SPORTS,
                COMPUTER,
                EDUCATION
            )
            val catList = mutableListOf<Category>()

            topics.map {
                val response = repository.getOnePicture(it)
                catList.add(Category(it, response.imageUrl ?: ""))
            }
            categories.postValue(catList)
            isProgressVisible.postValue(false)
        }
        isNetworkAvailable.value = context.isNetworkAvailable()
    }
}