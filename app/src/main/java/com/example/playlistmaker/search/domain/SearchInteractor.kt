package com.example.playlistmaker.search.domain

import com.example.playlistmaker.sharing.domain.Track

interface SearchInteractor {
    fun searchTrack(searchText: String, callback: (List<Track>) -> Unit)
    fun saveSearchHistory(track: Track)
    fun loadSearchHistory(): List<Track>
    fun clearSearchHistory()
}