package com.example.playlistmaker.media.domain

import com.example.playlistmaker.sharing.domain.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImagePath: String?,
        trackIds: String = "",
        trackCount: Int = 0
    )
    fun getAllPlaylists(): Flow<List<Playlist>>
}

