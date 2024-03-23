package com.example.playlistmaker.di

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackDbConverter
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import com.example.playlistmaker.media.domain.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.settings.data.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { SettingsRepository(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }
    factory { TrackDbConverter() }
    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
}

