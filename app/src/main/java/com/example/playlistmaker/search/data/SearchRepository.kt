package com.example.playlistmaker.search.data

import com.example.playlistmaker.sharing.data.ApiResponse
import com.example.playlistmaker.sharing.data.ApiService
import com.example.playlistmaker.sharing.domain.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepository(private val apiService: ApiService, private val searchHistory: SearchHistory) {

    fun searchTrack(searchText: String, callback: (List<Track>) -> Unit) {
        apiService.searchTrack(searchText).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    callback(response.body()?.results ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun saveSearchHistory(track: Track) {
        val history = loadSearchHistory().toMutableList()

        if (!history.any { it.trackId == track.trackId }) {
            if (history.size >= searchHistory.maxHistorySize) {
                history.removeAt(history.size - 1)
            }
            history.add(0, track)
            searchHistory.saveSearchHistory(history)
        }
    }

    fun loadSearchHistory(): List<Track> {
        return searchHistory.loadSearchHistory()
    }

    fun clearSearchHistory() {
        searchHistory.clearSearchHistory()
    }
}

