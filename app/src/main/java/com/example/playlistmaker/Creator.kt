package com.example.playlistmaker

import com.example.playlistmaker.data.MediaPlayerManagerImpl
import com.example.playlistmaker.domain.MediaPlayerManager

class Creator {
    fun getMediaPlayerManager(): MediaPlayerManager {
        return MediaPlayerManagerImpl()
    }
}