package com.example.playlistmaker.di

import android.content.Context.MODE_PRIVATE
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.player.data.MediaPlayerManagerImpl
import com.example.playlistmaker.player.domain.MediaPlayerManager
import com.example.playlistmaker.search.data.SearchHistory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { androidContext().getSharedPreferences("settings", MODE_PRIVATE) }
    single<MediaPlayerManager> { MediaPlayerManagerImpl() }
    single { SearchHistory(get()) }
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "playlist_database")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().favoriteTrackDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().playlistTrackDao() }


}
