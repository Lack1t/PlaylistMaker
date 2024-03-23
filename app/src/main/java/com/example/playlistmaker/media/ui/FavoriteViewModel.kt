package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
class FavoriteViewModel(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteScreenState>()
    val state: LiveData<FavoriteScreenState> = _state

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            favoriteTracksRepository.getAllFavoriteTracks()
                .onStart {
                    _state.value = FavoriteScreenState.Loading
                }
                .catch { e ->
                    _state.value = FavoriteScreenState.Error(e.message ?: "Unknown Error")
                }
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        _state.value = FavoriteScreenState.Error("No favorites found")
                    } else {
                        _state.value = FavoriteScreenState.FavoritesData(tracks)
                    }
                }
        }
    }
}
