package com.example.playlistmaker.media.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<List<Track>>()
    val favorites: LiveData<List<Track>> = _favorites

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    init {
        loadFavorites()
    }

   fun loadFavorites() {
        viewModelScope.launch {
            favoriteTracksRepository.getAllFavoriteTracks()
                .onStart {
                    _isLoading.value = true
                    _isError.value = false
                    Log.d("FavoriteViewModel", "Start loading favorite tracks")
                }
                .catch { e ->
                    _isLoading.value = false
                    _isError.value = true
                    Log.e("FavoriteViewModel", "Error loading favorite tracks: ${e.message}")
                }
                .collect { tracks ->
                    Log.d(
                        "FavoriteViewModel",
                        "Loading tracks. First track: ${tracks.firstOrNull()?.trackName}, Last track: ${tracks.lastOrNull()?.trackName}"
                    )
                    _favorites.value = tracks
                    _isLoading.value = false

                }
        }
    }
}
