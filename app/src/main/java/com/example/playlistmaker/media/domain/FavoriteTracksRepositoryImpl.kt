package com.example.playlistmaker.media.domain

import com.example.playlistmaker.data.db.FavoriteTrackDao
import com.example.playlistmaker.data.db.TrackDbConverter
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val favoriteTrackDao: FavoriteTrackDao
) : FavoriteTracksRepository {

    override fun getAllFavoriteTracks(): Flow<List<Track>> = flow {
        val dbTracks = favoriteTrackDao.getAllFavoriteTracks()
        emit(dbTracks.map { dbTrack -> TrackDbConverter.fromDbToDomainModel(dbTrack) })
    }

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTrackDao.insertTrack(TrackDbConverter.fromDomainModelToDb(track))
    }

    override suspend fun removeTrackFromFavorites(trackId: String) {
        favoriteTrackDao.deleteTrackById(trackId)
    }

    override suspend fun isTrackFavorite(trackId: String): Boolean {
        return favoriteTrackDao.countTrackById(trackId) > 0
    }
}
