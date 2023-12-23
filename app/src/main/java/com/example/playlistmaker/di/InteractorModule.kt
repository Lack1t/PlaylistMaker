package com.example.playlistmaker.di

import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.domain.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<SettingsInteractor> { SettingsInteractorImpl(get()) }
    single<SearchInteractor> { SearchInteractorImpl(get()) }
}