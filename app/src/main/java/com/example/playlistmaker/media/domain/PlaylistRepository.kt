package com.example.playlistmaker.media.domain
import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistRepository(private val playlistDao: PlaylistDao, private val playlistTrackDao: PlaylistTrackDao) {

    suspend fun createPlaylist(name: String, description: String, coverImagePath: String?) {
        val playlist = PlaylistEntity(
            title = name,
            description = description,
            coverImagePath = coverImagePath ?: "",
            trackIds = "",
            trackCount = 0
        )
        playlistDao.insert(playlist)
    }

    fun getAllPlaylists(): Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()

    suspend fun addTrackToPlaylist(playlistId: Int, trackId: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (playlistTrackDao.findTrackInPlaylist(trackId, playlistId) == null) {
                playlistTrackDao.insert(PlaylistTrackEntity(trackId = trackId, playlistId = playlistId))
                updatePlaylistTrackCount(playlistId)
                true
            } else {
                false
            }
        }
    }

    private suspend fun updatePlaylistTrackCount(playlistId: Int) {
        val currentPlaylist = playlistDao.getPlaylistById(playlistId)
        val updatedPlaylist = currentPlaylist.copy(trackCount = currentPlaylist.trackCount + 1)
        playlistDao.update(updatedPlaylist)
    }


}
