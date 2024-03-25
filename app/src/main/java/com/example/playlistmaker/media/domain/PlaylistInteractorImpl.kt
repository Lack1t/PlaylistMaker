package com.example.playlistmaker.media.domain

import com.example.playlistmaker.sharing.domain.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImagePath: String?,
        trackIds: String,
        trackCount: Int
    ) {
        repository.createPlaylist(name, description, coverImagePath)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }
}