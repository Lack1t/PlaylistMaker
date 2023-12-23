package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.SearchRepositoryImpl
import com.example.playlistmaker.sharing.domain.Track




class SearchInteractorImpl(private val repository: SearchRepositoryImpl) : SearchInteractor {

    override fun searchTrack(searchText: String, callback: (List<Track>) -> Unit) {
        repository.searchTrack(searchText, callback)
    }

    override fun saveSearchHistory(track: Track) {
        repository.saveSearchHistory(track)
    }

    override fun loadSearchHistory(): List<Track> {
        return repository.loadSearchHistory()
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}
