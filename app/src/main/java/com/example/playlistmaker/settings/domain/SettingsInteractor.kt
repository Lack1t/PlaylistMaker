package com.example.playlistmaker.settings.domain


interface SettingsInteractor {
    fun getDarkThemeState(): Boolean
    fun saveDarkThemeState(isDarkTheme: Boolean)
}
