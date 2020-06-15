package com.georgcantor.wallpaperapp.di

import com.georgcantor.wallpaperapp.model.local.FavDatabase
import com.georgcantor.wallpaperapp.model.remote.ApiClient
import com.georgcantor.wallpaperapp.repository.Repository
import com.georgcantor.wallpaperapp.util.PreferenceManager
import com.georgcantor.wallpaperapp.view.activity.categories.CategoriesViewModel
import com.georgcantor.wallpaperapp.view.activity.detail.DetailViewModel
import com.georgcantor.wallpaperapp.view.activity.models.ModelsViewModel
import com.georgcantor.wallpaperapp.view.activity.search.SearchViewModel
import com.georgcantor.wallpaperapp.view.fragment.favorites.FavoritesViewModel
import com.georgcantor.wallpaperapp.view.fragment.pictures.PicturesViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val apiModule = module {
    single { ApiClient.create(get()) }
}

val dbModule = module {
    single { FavDatabase.buildDefault(get()).dao() }
}

val repositoryModule = module {
    single { Repository(get(), get()) }
}

val preferenceModule = module {
    single { PreferenceManager(androidApplication().applicationContext) }
}

val viewModelModule = module {
    viewModel {
        PicturesViewModel(androidApplication(), get(), get())
    }
    viewModel {
        DetailViewModel(androidApplication(), get())
    }
    viewModel {
        FavoritesViewModel(get())
    }
    viewModel {
        CategoriesViewModel(androidApplication(), get())
    }
    viewModel {
        SearchViewModel(androidApplication(), get())
    }
    viewModel {
        ModelsViewModel(androidApplication(), get())
    }
}