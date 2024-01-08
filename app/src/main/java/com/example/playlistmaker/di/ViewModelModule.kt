package com.example.playlistmaker.di
import com.example.playlistmaker.media.ui.FavoriteViewModel
import com.example.playlistmaker.media.ui.PlaylistViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SettingsViewModel(get()) }
    viewModel { PlayerViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { FavoriteViewModel() }
    viewModel { PlaylistViewModel() }
}

