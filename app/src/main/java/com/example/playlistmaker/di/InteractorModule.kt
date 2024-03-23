package com.example.playlistmaker.di

import com.example.playlistmaker.media.domain.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.FavoriteTracksInteractorImpl
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.domain.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<SettingsInteractor> { SettingsInteractorImpl(get()) }
    factory<SearchInteractor> { SearchInteractorImpl(get()) }
    factory<FavoriteTracksInteractor> { FavoriteTracksInteractorImpl(get())}

}
