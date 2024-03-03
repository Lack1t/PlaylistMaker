package com.example.playlistmaker.search.domain

import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTrack(searchText: String): Flow<List<Track>>

    suspend fun saveSearchHistory(track: Track)
    suspend fun loadSearchHistory(): List<Track>
    suspend fun clearSearchHistory()
}
