package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchRepository
import com.example.playlistmaker.sharing.data.ApiService
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers

class SearchRepositoryImpl(
    private val apiService: ApiService,
    private val searchHistory: SearchHistory
) : SearchRepository {

    override fun searchTrack(searchText: String): Flow<List<Track>> = flow {

        try {
            val response = apiService.searchTrack(searchText)
            emit(response.results)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun saveSearchHistory(track: Track) {
        val history = loadSearchHistory().toMutableList()

        if (!history.any { it.trackId == track.trackId }) {
            if (history.size >= searchHistory.maxHistorySize) {
                history.removeAt(history.size - 1)
            }
            history.add(0, track)
            searchHistory.saveSearchHistory(history)
        }
    }

    override suspend fun loadSearchHistory(): List<Track> {
        return searchHistory.loadSearchHistory()
    }

    override suspend fun clearSearchHistory() {
        searchHistory.clearSearchHistory()
    }
}
