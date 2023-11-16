package com.example.playlistmaker.domain
interface MediaPlayerManager {
    fun prepareMediaPlayer(previewUrl: String)
    fun startPlayback()
    fun pausePlayback()
    fun stopPlayback()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Long
    fun setOnCompletionListener(listener: () -> Unit)
    fun release()
}