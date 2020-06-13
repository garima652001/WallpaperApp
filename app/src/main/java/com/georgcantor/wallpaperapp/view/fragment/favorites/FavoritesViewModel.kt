package com.georgcantor.wallpaperapp.view.fragment.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.repository.Repository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: Repository) : ViewModel() {

    val isProgressVisible = MutableLiveData<Boolean>().apply { this.value = true }
    val error = MutableLiveData<String>()
    val favorites = MutableLiveData<List<Favorite>>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        error.postValue(throwable.message)
        isProgressVisible.postValue(false)
    }

    fun getFavorites() {
        viewModelScope.launch(exceptionHandler) {
            val favList = repository.getAllAsync().await()
            favorites.postValue(favList)
            isProgressVisible.postValue(false)
        }
    }
}