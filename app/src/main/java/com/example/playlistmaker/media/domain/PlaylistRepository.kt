package com.example.playlistmaker.media.domain

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistDbConverter.Companion.toDomainModel
import com.example.playlistmaker.data.db.PlaylistDbConverter.Companion.toEntity
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.sharing.domain.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

class PlaylistRepository(
    private val context: Context,
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao
) {
    suspend fun createPlaylist(name: String, description: String?, coverImageUri: String?) {
        val copiedFilePath = coverImageUri?.let { uri ->
            copyFileToInternalStorage(Uri.parse(uri), "coverImage_${System.currentTimeMillis()}.jpg")
        }

        val playlist = Playlist(title = name, description = description, coverImagePath = copiedFilePath, trackIds = "", trackCount = 0)
        withContext(Dispatchers.IO) {
            playlistDao.insert(playlist.toEntity())
        }
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists().map { entities ->
        entities.map { it.toDomainModel() }
    }

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
        withContext(Dispatchers.IO) {
            val currentPlaylistEntity = playlistDao.getPlaylistById(playlistId)
            val updatedPlaylistEntity = currentPlaylistEntity.copy(trackCount = currentPlaylistEntity.trackCount + 1)
            playlistDao.update(updatedPlaylistEntity)
        }
    }

    private fun copyFileToInternalStorage(uri: Uri, newFileName: String): String {
        Log.d("PlaylistRepository", "Copying file from URI: $uri to internal storage with name: $newFileName")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val newFile = File(context.filesDir, newFileName).apply {
                outputStream().use { fileOut ->
                    inputStream.copyTo(fileOut)
                }
            }
            val copiedFilePath = newFile.absolutePath
            Log.d("PlaylistRepository", "File copied successfully to: $copiedFilePath")
            return newFile.absolutePath
        }
        Log.e("PlaylistRepository", "Failed to copy file from URI: $uri")
        throw IllegalArgumentException("Failed to copy file")
    }
}
