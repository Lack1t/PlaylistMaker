package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.settings.data.SettingsRepository


class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {

    override fun getDarkThemeState(): Boolean {
        return repository.getDarkThemeState()
    }

    override fun saveDarkThemeState(isDarkTheme: Boolean) {
        repository.saveDarkThemeState(isDarkTheme)
    }
}
