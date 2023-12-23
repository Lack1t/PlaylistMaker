package com.example.playlistmaker.settings.data


import android.content.SharedPreferences

class SettingsRepository(private val sharedPreferences: SharedPreferences) {

    fun getDarkThemeState(): Boolean {
        return sharedPreferences.getBoolean("darkTheme", false)
    }

    fun saveDarkThemeState(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean("darkTheme", isDarkTheme).apply()
    }
}
