package com.example.playlistmaker.media.domain

import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(private val repository: FavoriteTracksRepository): FavoriteTracksInteractor {
    override suspend fun addTrackToFavorites(track: Track) = repository.addTrackToFavorites(track)

    override suspend fun removeTrackFromFavorites(trackId: String) = repository.removeTrackFromFavorites(trackId)

    override fun getAllFavoriteTracks(): Flow<List<Track>> = repository.getAllFavoriteTracks()
}
