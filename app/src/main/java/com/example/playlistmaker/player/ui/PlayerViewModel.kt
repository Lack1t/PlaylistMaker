package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import com.example.playlistmaker.player.domain.MediaPlayerManager
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PlayerViewModel(
    private val mediaPlayerManager: MediaPlayerManager,
    private val favoriteTracksRepository: FavoriteTracksRepository
) : ViewModel(), CoroutineScope {
    private val _trackData = MutableLiveData<Track>()
    val trackData: LiveData<Track> = _trackData

    private val _playStatus = MutableLiveData<Boolean>().apply { value = false }
    val playStatus: LiveData<Boolean> = _playStatus

    private val _trackPosition = MutableLiveData<Long>()
    val trackPosition: LiveData<Long> = _trackPosition

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private var playerJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + (playerJob ?: SupervisorJob())

    fun loadTrack(track: Track) {
        _trackData.value = track
        if (track.previewUrl.isNotEmpty()) {
            mediaPlayerManager.prepareMediaPlayer(track.previewUrl)
        } else {
            // when previewUrl is null or empty
        }
        _playStatus.postValue(false)
        checkIfTrackIsFavorite(track.trackId!!)
    }

    private fun checkIfTrackIsFavorite(trackId: String) {
        viewModelScope.launch {
            val isFav = favoriteTracksRepository.isTrackFavorite(trackId)
            _isFavorite.postValue(isFav)
        }
    }

    fun playOrPause() {
        if (mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.pausePlayback()
            _playStatus.value = false
        } else {
            mediaPlayerManager.startPlayback()
            _playStatus.value = true
            startTrackingPosition()
        }
    }

    fun onFavoriteClicked() {
        val currentTrack = _trackData.value ?: return
        viewModelScope.launch {
            if (_isFavorite.value == true) {
                favoriteTracksRepository.removeTrackFromFavorites(currentTrack.trackId!!)
                _isFavorite.postValue(false)
            } else {
                favoriteTracksRepository.addTrackToFavorites(currentTrack)
                _isFavorite.postValue(true)
            }
        }
    }

    fun startTrackingPosition() {
        playerJob?.cancel()
        playerJob = launch {
            while (isActive) {
                if (mediaPlayerManager.isPlaying()) {
                    _trackPosition.postValue(mediaPlayerManager.getCurrentPosition())
                } else {
                    stopTrackingPosition()
                    _trackPosition.postValue(0)
                    _playStatus.postValue(false)
                }
                delay(300)
            }
        }
    }

    private fun stopTrackingPosition() {
        playerJob?.cancel()
    }

    fun releaseResources() {
        mediaPlayerManager.release()
        playerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        releaseResources()
    }

    fun isPlaying(): Boolean {
        return mediaPlayerManager.isPlaying()
    }
}
