package com.example.playlistmaker.player.ui
import com.example.playlistmaker.media.domain.PlaylistRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.db.PlaylistEntity
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
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class PlayerViewModel(
    private val mediaPlayerManager: MediaPlayerManager,
    private val favoriteTracksRepository: FavoriteTracksRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel(), CoroutineScope {
    private val _trackData = MutableLiveData<Track>()
    val trackData: LiveData<Track> = _trackData

    private val _playStatus = MutableLiveData<Boolean>().apply { value = false }
    val playStatus: LiveData<Boolean> = _playStatus

    private val _trackPosition = MutableLiveData<Long>()
    val trackPosition: LiveData<Long> = _trackPosition

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite
    private val _currentTrack = MutableLiveData<Track?>()
    val currentTrack: LiveData<Track?> = _currentTrack

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message
    private val _playlists = MutableLiveData<List<PlaylistEntity>>()
    val playlists: LiveData<List<PlaylistEntity>> = _playlists

    private var playerJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + (playerJob ?: SupervisorJob())

    fun loadTrack(track: Track) {
        _trackData.value = track
        _currentTrack.value = track
        if (track.previewUrl.isNotEmpty()) {
            mediaPlayerManager.prepareMediaPlayer(track.previewUrl)
        }
        _playStatus.postValue(false)
        checkIfTrackIsFavorite(track.trackId)
    }

    private fun checkIfTrackIsFavorite(trackId: String?) {
        trackId?.let {
            viewModelScope.launch {
                val isFav = favoriteTracksRepository.isTrackFavorite(it)
                _isFavorite.postValue(isFav)
            }
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
        currentTrack.trackId?.let { trackId ->
            viewModelScope.launch {
                if (_isFavorite.value == true) {
                    favoriteTracksRepository.removeTrackFromFavorites(trackId)
                    _isFavorite.postValue(false)
                } else {
                    favoriteTracksRepository.addTrackToFavorites(currentTrack)
                    _isFavorite.postValue(true)
                }
            }
        }
    }

    fun addTrackToPlaylist(playlistId: Int, trackId: String, playlistName: String) {
        viewModelScope.launch {
            val added = playlistRepository.addTrackToPlaylist(playlistId, trackId)
            if (added) {
                _message.value = "Добавлено в плейлист $playlistName"
            } else {
                _message.value = "Трек уже добавлен в плейлист $playlistName"
            }
        }
    }
    fun startTrackingPosition() {
        playerJob?.cancel()
        playerJob = launch {
            while (isActive) {
                val position = mediaPlayerManager.getCurrentPosition()
                _trackPosition.postValue(position)
                delay(300) // Update frequency
            }
        }
    }

    private fun stopTrackingPosition() {
        playerJob?.cancel()
        _trackPosition.postValue(0)
        _playStatus.postValue(false)
    }

    fun releaseResources() {
        mediaPlayerManager.release()
        stopTrackingPosition()
    }

    override fun onCleared() {
        super.onCleared()
        releaseResources()
    }

    fun isPlaying(): Boolean {
        return mediaPlayerManager.isPlaying()
    }
    fun loadPlaylists() {
        Log.d("PlayerViewModel", "loadPlaylists called")
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.getAllPlaylists().collect { playlists ->
                Log.d("PlayerViewModel", "Loading playlists: ${playlists.size} found")
                withContext(Dispatchers.Main) {
                    _playlists.postValue(playlists)
                }
            }
        }
    }
    }


