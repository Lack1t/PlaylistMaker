package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlistTrack: PlaylistTrackEntity)
    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId AND playlistId = :playlistId LIMIT 1")
    suspend fun findTrackInPlaylist(trackId: String, playlistId: Int): PlaylistTrackEntity?
}


