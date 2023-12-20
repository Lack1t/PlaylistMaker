package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.MediaPlayerManager
import com.example.playlistmaker.player.domain.PlayerViewModel

@Suppress("UNCHECKED_CAST")
class PlayerViewModelFactory(private val mediaPlayerManager: MediaPlayerManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(mediaPlayerManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
