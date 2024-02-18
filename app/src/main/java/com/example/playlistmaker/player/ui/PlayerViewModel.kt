package com.example.playlistmaker.player.ui
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.MediaPlayerManager
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PlayerViewModel(private val mediaPlayerManager: MediaPlayerManager) : ViewModel(), CoroutineScope {
    private val _trackData = MutableLiveData<Track>()
    val trackData: LiveData<Track> = _trackData

    private val _playStatus = MutableLiveData<Boolean>().apply { value = false }
    val playStatus: LiveData<Boolean> = _playStatus

    private val _trackPosition = MutableLiveData<Long>()
    val trackPosition: LiveData<Long> = _trackPosition

    private var playerJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + (playerJob ?: SupervisorJob())

    fun loadTrack(track: Track) {
        _trackData.value = track
        mediaPlayerManager.prepareMediaPlayer(track.previewUrl)
        _playStatus.postValue(false)
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

    fun releaseResources() {
        mediaPlayerManager.release()
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

    override fun onCleared() {
        super.onCleared()
        playerJob?.cancel()
        mediaPlayerManager.release()
    }

    fun isPlaying(): Boolean {
        return mediaPlayerManager.isPlaying()
    }
}
