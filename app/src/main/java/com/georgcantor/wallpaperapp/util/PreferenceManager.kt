package com.georgcantor.wallpaperapp.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.georgcantor.wallpaperapp.util.Constants.MAIN_STORAGE

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(MAIN_STORAGE, MODE_PRIVATE)

    fun saveString(key: String, value: String) = prefs.edit().putString(key, value).apply()

    fun getString(key: String): String? = prefs.getString(key, "")

    fun saveInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()

    fun getInt(key: String): Int? = prefs.getInt(key, 0)
}