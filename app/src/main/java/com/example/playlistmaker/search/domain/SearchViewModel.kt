package com.example.playlistmaker.search.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.sharing.domain.Track
import com.example.playlistmaker.search.data.SearchRepository

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun searchTracks(searchText: String) {
        repository.searchTrack(searchText) { result ->
            _tracks.postValue(result)
        }
    }

    fun addTrackToSearchHistory(track: Track) {
        repository.saveSearchHistory(track)
    }

    fun loadSearchHistory() {
        _tracks.postValue(repository.loadSearchHistory())
    }

    fun clearSearchHistory() {
        repository.clearSearchHistory()
        _tracks.postValue(emptyList())
    }

}
