package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.settings.data.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { SettingsRepository(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }
}
