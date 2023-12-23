package com.example.playlistmaker.player.data


import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.MediaPlayerManager


class MediaPlayerManagerImpl : MediaPlayerManager {

    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private var onCompletionListener: (() -> Unit)? = null

    init {
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            setOnCompletionListener {
                onCompletionListener?.invoke()
            }
        }
    }

    override fun prepareMediaPlayer(previewUrl: String) {
        mediaPlayer?.apply {
            if (isPlaying || isPrepared) {
                reset()
            }
            setDataSource(previewUrl)
            prepare()
            isPrepared = true
        } ?: run {
            initializeMediaPlayer()
            prepareMediaPlayer(previewUrl)
        }
    }

    override fun startPlayback() {
        mediaPlayer?.apply {
            if (isPrepared && !isPlaying) {
                start()
            }
        }
    }

    override fun pausePlayback() {
        mediaPlayer?.apply {
            if (isPrepared && isPlaying) {
                pause()
            }
        }
    }

    override fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPrepared && isPlaying) {
                stop()
                isPrepared = false
            }
        }
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true && isPrepared
    }

    override fun getCurrentPosition(): Long {
        return if (isPlaying()) {
            mediaPlayer?.currentPosition?.toLong() ?: 0L
        } else {
            0L
        }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        this.onCompletionListener = listener
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }
}
