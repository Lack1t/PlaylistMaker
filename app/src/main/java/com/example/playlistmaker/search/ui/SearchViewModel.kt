package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(private val interactor: SearchInteractor) : ViewModel() {
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks
    private val _isSearchHistoryAvailable = MutableLiveData<Boolean>()
    val isSearchHistoryAvailable: LiveData<Boolean> = _isSearchHistoryAvailable

    fun searchTracks(searchText: String) {
        viewModelScope.launch {
            interactor.searchTrack(searchText)
                .catch { _ ->

                }
                .asLiveData(viewModelScope.coroutineContext)
                .observeForever { result ->
                    _tracks.postValue(result)
                }
        }
    }

    fun addTrackToSearchHistory(track: Track) {
        viewModelScope.launch {
            interactor.saveSearchHistory(track)
        }
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            val history = interactor.loadSearchHistory()
            _tracks.postValue(history)
            _isSearchHistoryAvailable.postValue(history.isNotEmpty())
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            interactor.clearSearchHistory()
            _tracks.postValue(emptyList())
            _isSearchHistoryAvailable.postValue(false)
        }
    }
}
