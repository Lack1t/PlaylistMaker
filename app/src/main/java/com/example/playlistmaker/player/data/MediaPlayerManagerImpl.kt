package com.example.playlistmaker.player.data


import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.MediaPlayerManager


class MediaPlayerManagerImpl : MediaPlayerManager {

    private val mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }

    private var isPrepared = false
    private var onCompletionListener: (() -> Unit)? = null

    init {
        mediaPlayer.setOnCompletionListener {
            onCompletionListener?.invoke()
        }
    }

    override fun prepareMediaPlayer(previewUrl: String) {
        mediaPlayer.apply {
            reset()
            setDataSource(previewUrl)
            prepare()
            isPrepared = true
        }
    }

    override fun startPlayback() {
        if (isPrepared && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun pausePlayback() {
        if (isPrepared && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun stopPlayback() {
        if (isPrepared && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            isPrepared = false
        }
    }

    override fun isPlaying(): Boolean {
        return isPrepared && mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Long {
        return if (isPlaying()) {
            mediaPlayer.currentPosition.toLong()
        } else {
            0L
        }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        this.onCompletionListener = listener
    }

    override fun release() {
        mediaPlayer.release()
        isPrepared = false
    }
}
