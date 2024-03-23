package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteTrackDao {
    @Insert
    suspend fun insertTrack(track: FavoriteTrack)

    @Delete
    suspend fun deleteTrack(track: FavoriteTrack)

    @Query("SELECT * FROM favorite_tracks ORDER BY addedTimestamp DESC")
    suspend fun getAllFavoriteTracks(): List<FavoriteTrack>

    @Query("DELETE FROM favorite_tracks WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: String)
    @Query("SELECT COUNT(trackId) FROM favorite_tracks WHERE trackId = :trackId")
    suspend fun countTrackById(trackId: String): Int
}
