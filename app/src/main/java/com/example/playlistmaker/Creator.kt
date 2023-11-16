package com.example.playlistmaker

import com.example.playlistmaker.data.MediaPlayerManager
import com.example.playlistmaker.domain.MediaPlayerCreator
import com.example.playlistmaker.domain.MediaPlayerUseCase

class Creator {
    private val mediaPlayerCreator: MediaPlayerCreator = MediaPlayerManager()

    fun getMediaPlayerUseCase(): MediaPlayerUseCase {
        return mediaPlayerCreator.createMediaPlayer()
    }
}