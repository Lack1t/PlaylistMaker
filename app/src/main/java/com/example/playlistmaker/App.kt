package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        initTheme()
    }


    private fun initTheme() {
        updateTheme()
    }

    private fun updateTheme() {
        val nightMode = if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
