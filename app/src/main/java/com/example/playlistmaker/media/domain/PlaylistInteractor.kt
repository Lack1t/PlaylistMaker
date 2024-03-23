package com.example.playlistmaker.media.domain

import com.example.playlistmaker.data.db.PlaylistEntity
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, coverImagePath: String?)
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
}

