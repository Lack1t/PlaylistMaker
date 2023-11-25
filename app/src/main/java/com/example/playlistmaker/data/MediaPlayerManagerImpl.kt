package com.example.playlistmaker.data


import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlistmaker.domain.MediaPlayerManager

class MediaPlayerManagerImpl : MediaPlayerManager {

    private val mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }

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
        }
    }

    override fun startPlayback() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun pausePlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun stopPlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        this.onCompletionListener = listener
    }

    override fun release() {
        mediaPlayer.release()
    }
}
