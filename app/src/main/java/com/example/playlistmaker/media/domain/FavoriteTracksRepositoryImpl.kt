package com.example.playlistmaker.media.domain


import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackDbConverter
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoriteTracksRepository {


    override fun getAllFavoriteTracks(): Flow<List<Track>> = flow {
        val dbTracks = appDatabase.favoriteTrackDao().getAllFavoriteTracks()
        emit(dbTracks.map { dbTrack -> TrackDbConverter.fromDbToDomainModel(dbTrack) })
    }

    override suspend fun addTrackToFavorites(track: Track) {
        appDatabase.favoriteTrackDao().insertTrack(TrackDbConverter.fromDomainModelToDb(track))
    }

    override suspend fun removeTrackFromFavorites(trackId: String) {
        appDatabase.favoriteTrackDao().deleteTrackById(trackId)
    }
    override suspend fun isTrackFavorite(trackId: String): Boolean {
        return appDatabase.favoriteTrackDao().countTrackById(trackId) > 0
    }
}

