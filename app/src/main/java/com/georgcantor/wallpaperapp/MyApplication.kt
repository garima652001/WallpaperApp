package com.georgcantor.wallpaperapp

import android.app.Application
import com.georgcantor.wallpaperapp.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(
                    apiModule,
                    dbModule,
                    repositoryModule,
                    preferenceModule,
                    viewModelModule
                )
            )
        }
    }
}
