package com.example.playlistmaker.player.domain

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.sharing.domain.Track

class PlayerViewModel(private val mediaPlayerManager: MediaPlayerManager) : ViewModel()  {
    private val _trackData = MutableLiveData<Track>()
    val trackData: LiveData<Track> = _trackData

    private val _playStatus = MutableLiveData<Boolean>().apply { value = false }
    val playStatus: LiveData<Boolean> = _playStatus

    private val _trackPosition = MutableLiveData<Long>()
    val trackPosition: LiveData<Long> = _trackPosition

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


    fun pausePlaybackIfNeeded() {
        if (mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.pausePlayback()
            _playStatus.value = false
        }
    }

    fun releaseResources() {
        mediaPlayerManager.release()
    }

    private val positionUpdateHandler = Handler(Looper.getMainLooper())
    private var positionUpdateRunnable: Runnable? = null

    private fun startTrackingPosition() {
        positionUpdateRunnable?.let { positionUpdateHandler.removeCallbacks(it) }

        positionUpdateRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayerManager.isPlaying()) {
                    _trackPosition.postValue(mediaPlayerManager.getCurrentPosition())
                    positionUpdateHandler.postDelayed(this, 1000)
                }
            }
        }
        positionUpdateHandler.post(positionUpdateRunnable!!)
    }


    override fun onCleared() {
        super.onCleared()
        positionUpdateRunnable?.let { positionUpdateHandler.removeCallbacks(it) }
        mediaPlayerManager.release()
    }
    fun isPlaying(): Boolean {
        return mediaPlayerManager.isPlaying()
    }
    fun getCurrentPosition(): Long {
        return mediaPlayerManager.getCurrentPosition()
    }
}
