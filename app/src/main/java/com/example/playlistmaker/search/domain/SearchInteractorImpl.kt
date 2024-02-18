package com.example.playlistmaker.search.domain

import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.Flow

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    override fun searchTrack(searchText: String): Flow<List<Track>> {
        return repository.searchTrack(searchText)
    }

    override suspend fun saveSearchHistory(track: Track) {
        repository.saveSearchHistory(track)
    }

    override suspend fun loadSearchHistory(): List<Track> {
        return repository.loadSearchHistory()
    }

    override suspend fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}
