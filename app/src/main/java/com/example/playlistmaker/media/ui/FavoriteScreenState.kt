package com.example.playlistmaker.media.ui

import com.example.playlistmaker.sharing.domain.Track

sealed interface FavoriteScreenState {
    data object Loading : FavoriteScreenState
    data class FavoritesData(val tracks: List<Track>) : FavoriteScreenState
    data class Error(val message: String) : FavoriteScreenState
}
