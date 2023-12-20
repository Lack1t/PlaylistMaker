package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.sharing.domain.Track

class SearchViewModel(private val interactor: SearchInteractor) : ViewModel() {
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun searchTracks(searchText: String) {
        interactor.searchTrack(searchText) { result ->
            _tracks.postValue(result)
        }
    }

    fun addTrackToSearchHistory(track: Track) {
        interactor.saveSearchHistory(track)
    }

    fun loadSearchHistory() {
        _tracks.postValue(interactor.loadSearchHistory())
    }

    fun clearSearchHistory() {
        interactor.clearSearchHistory()
        _tracks.postValue(emptyList())
    }
}
