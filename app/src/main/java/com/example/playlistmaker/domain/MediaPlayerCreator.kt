package com.example.playlistmaker.domain


interface MediaPlayerCreator {
    fun createMediaPlayer(): MediaPlayerUseCase
}