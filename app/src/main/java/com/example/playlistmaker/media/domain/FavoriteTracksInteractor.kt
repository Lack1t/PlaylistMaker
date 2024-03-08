package com.example.playlistmaker.media.domain
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(trackId: String)
    fun getAllFavoriteTracks(): Flow<List<Track>>
}
