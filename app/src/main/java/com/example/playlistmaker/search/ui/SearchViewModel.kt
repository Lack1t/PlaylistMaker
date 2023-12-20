package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.sharing.domain.Track

class SearchViewModel(private val interactor: SearchInteractor) : ViewModel()  {
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks
    private val _isSearchHistoryAvailable = MutableLiveData<Boolean>()
    val isSearchHistoryAvailable: LiveData<Boolean> = _isSearchHistoryAvailable
    fun searchTracks(searchText: String) {
        interactor.searchTrack(searchText) { result ->
            _tracks.postValue(result)
        }
    }

    fun addTrackToSearchHistory(track: Track) {
        interactor.saveSearchHistory(track)
    }

    fun loadSearchHistory() {
        val history = interactor.loadSearchHistory()
        _tracks.postValue(history)
        _isSearchHistoryAvailable.postValue(history.isNotEmpty())
    }

    fun clearSearchHistory() {
        interactor.clearSearchHistory()
        _tracks.postValue(emptyList())
        _isSearchHistoryAvailable.postValue(false)
    }
    fun hasSearchHistory(): Boolean {
        val history = interactor.loadSearchHistory()
        return history.isNotEmpty()
    }
}
