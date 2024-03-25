package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.sharing.domain.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        fetchPlaylists()
    }

    fun createPlaylist(name: String, description: String?, coverImagePath: String?) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(name, description, coverImagePath)
            fetchPlaylists()
        }
    }


    private fun fetchPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlistsList ->
                _playlists.postValue(playlistsList)
            }
        }
    }
}
