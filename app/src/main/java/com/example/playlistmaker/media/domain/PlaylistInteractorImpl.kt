package com.example.playlistmaker.media.domain

import com.example.playlistmaker.data.db.PlaylistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) : PlaylistInteractor {
    override suspend fun createPlaylist(name: String, description: String, coverImagePath: String?) = withContext(Dispatchers.IO) {
        playlistRepository.createPlaylist(name, description, coverImagePath)
    }

    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> = playlistRepository.getAllPlaylists()
}

