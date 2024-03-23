package com.example.playlistmaker.media.domain

import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(trackId: String)
    fun getAllFavoriteTracks(): Flow<List<Track>>
    suspend fun isTrackFavorite(trackId: String): Boolean
}