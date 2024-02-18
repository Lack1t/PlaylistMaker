package com.example.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(private val interactor: SearchInteractor) : ViewModel() {
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks
    private val _isSearchHistoryAvailable = MutableStateFlow(false)
    val isSearchHistoryAvailable: StateFlow<Boolean> = _isSearchHistoryAvailable

    fun searchTracks(searchText: String) {
        viewModelScope.launch {
            interactor.searchTrack(searchText)
                .catch { _ ->
                }
                .collect { result ->
                    _tracks.value = result
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
            _tracks.value = history
            _isSearchHistoryAvailable.value = history.isNotEmpty()
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            interactor.clearSearchHistory()
            _tracks.value = emptyList()
            _isSearchHistoryAvailable.value = false
        }
    }
}
