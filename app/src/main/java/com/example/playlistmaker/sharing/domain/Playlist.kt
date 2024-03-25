package com.example.playlistmaker.sharing.domain

data class Playlist(
    val id: Int = 0,
    val title: String,
    val description: String?,
    val coverImagePath: String?,
    val trackIds: String,
    val trackCount: Int = 0
)