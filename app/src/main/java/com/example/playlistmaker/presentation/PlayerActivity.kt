package com.example.playlistmaker.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.MediaPlayerManager
import com.example.playlistmaker.domain.MediaPlayerUseCase
import com.example.playlistmaker.domain.Track

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity() {

    private lateinit var btnPlayPause: ImageButton
    private lateinit var progressTime: TextView
    private lateinit var handler: Handler
    private val mediaPlayerManager: MediaPlayerUseCase = MediaPlayerManager()
    private var isTrackingTime = false

    private val updateSeekBar = Runnable { startTrackingTime() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = intent.getSerializableExtra("track") as? Track
        btnPlayPause = findViewById(R.id.btnPlay)
        progressTime = findViewById(R.id.progressTime)
        handler = Handler(Looper.getMainLooper())

        if (track != null) {
            fillTrackData(track)
            track.previewUrl?.let {
                mediaPlayerManager.prepareMediaPlayer(it)
            }
        }

        btnPlayPause.setOnClickListener {
            if (mediaPlayerManager.isPlaying()) {
                mediaPlayerManager.pausePlayback()
            } else {
                mediaPlayerManager.startPlayback()
            }
            updateButtonImage()
        }

        findViewById<Button>(R.id.btnPlayerBack).setOnClickListener {
            onBackPressed()
        }

        mediaPlayerManager.setOnCompletionListener {
            isTrackingTime = false
            progressTime.text = "00:00"
            updateButtonImage()
        }
    }

    private fun startTrackingTime() {
        if (mediaPlayerManager.isPlaying()) {
            val currentPosition = mediaPlayerManager.getCurrentPosition()
            progressTime.text = formatTrackDuration(currentPosition)
            handler.postDelayed(updateSeekBar, 1000)
        }
    }

    private fun updateButtonImage() {
        val imageResource = if (mediaPlayerManager.isPlaying()) R.drawable.button_pause else R.drawable.button_play
        btnPlayPause.setImageResource(imageResource)
    }

    private fun fillTrackData(track: Track) {
        findViewById<ImageView>(R.id.track_image).apply {
            Glide.with(this@PlayerActivity)
                .load(Track.getCoverArtwork(track.artworkUrl100))
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(8))
                .into(this)
        }
        findViewById<TextView>(R.id.trackNameResult).text = track.trackName
        findViewById<TextView>(R.id.artistNameResult).text = track.artistName
        findViewById<TextView>(R.id.collection_Name).text = track.collectionName
        findViewById<TextView>(R.id.release_Date).text = Track.formatReleaseDate(track.releaseDate)
        findViewById<TextView>(R.id.primary_GenreName).text = track.primaryGenreName
        findViewById<TextView>(R.id.country).text = track.country
        val trackTimeMillis = track.trackTimeMillis.toLongOrNull() ?: 0L
        findViewById<TextView>(R.id.trackTimeResult).text = Track.formatTrackDuration(trackTimeMillis)
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.stopPlayback()
        }
        (mediaPlayerManager as? MediaPlayerManager)?.release()
        handler.removeCallbacks(updateSeekBar)
    }

    private fun formatTrackDuration(duration: Long): String {
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }
}
