package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean>
        get() = _isDarkTheme

    init {
        _isDarkTheme.value = settingsInteractor.getDarkThemeState()
    }

    fun toggleDarkTheme() {
        val newThemeState = !_isDarkTheme.value!!
        _isDarkTheme.value = newThemeState
        settingsInteractor.saveDarkThemeState(newThemeState)
    }
}
