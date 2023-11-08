package com.example.playlistmaker

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btnPlayPause: ImageButton
    private lateinit var trackDurationView: TextView
    private lateinit var handler: Handler
    private lateinit var progressTime: TextView
    private var isTrackingTime = false

    private val updateSeekBar = Runnable { startTrackingTime() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = intent.getSerializableExtra("track") as? Track
        btnPlayPause = findViewById(R.id.btnPlay)
        trackDurationView = findViewById(R.id.trackTimeResult)
        progressTime = findViewById(R.id.progressTime)
        handler = Handler(Looper.getMainLooper())

        mediaPlayer = MediaPlayer()
        initMediaPlayer()

        if (track != null) {
            fillTrackData(track)
            track.previewUrl?.let {
                prepareMediaPlayer(it)
            }
        }

        btnPlayPause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                pausePlayback()
            } else {
                startPlayback()
            }
        }

        val backButton = findViewById<Button>(R.id.btnPlayerBack)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initMediaPlayer() {
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        mediaPlayer.setOnCompletionListener {
            if (mediaPlayer.isPlaying) {
                stopPlayback()
            }
            progressTime.text = "00:00"
            updateButtonImage()
            mediaPlayer.seekTo(0)
        }
    }




    private fun prepareMediaPlayer(previewUrl: String) {
        try {
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                btnPlayPause.isEnabled = true
                isTrackingTime = true
                startTrackingTime()
            }
        } catch (e: Exception) {
            Log.e("PlayerActivity", "Error initializing MediaPlayer", e)
            btnPlayPause.isEnabled = false
        }
    }

    private fun startPlayback() {
        mediaPlayer.start()
        isTrackingTime = true
        startTrackingTime()
        updateButtonImage()
    }

    private fun pausePlayback() {
        mediaPlayer.pause()
        stopTrackingTime()
        updateButtonImage()
    }

    private fun stopPlayback() {
        mediaPlayer.pause()
        isTrackingTime = false
        stopTrackingTime()
        updateButtonImage()
    }

    private fun startTrackingTime() {
        if (isTrackingTime) {
            val currentPosition = mediaPlayer.currentPosition.toLong()
            val formattedTime = Track.formatTrackDuration(currentPosition)
            runOnUiThread {
                progressTime.text = formattedTime
            }
            handler.postDelayed(updateSeekBar, 1000)
        }
    }

    private fun stopTrackingTime() {
        handler.removeCallbacks(updateSeekBar)
    }

    private fun updateButtonImage() {
        val imageResource = if (mediaPlayer.isPlaying) R.drawable.button_pause else R.drawable.button_play
        btnPlayPause.setImageResource(imageResource)
    }

    private fun fillTrackData(track: Track) {
        val trackImage = findViewById<ImageView>(R.id.track_image)
        val trackName = findViewById<TextView>(R.id.trackNameResult)
        val artistName = findViewById<TextView>(R.id.artistNameResult)
        val collectionName = findViewById<TextView>(R.id.collection_Name)
        val releaseDate = findViewById<TextView>(R.id.release_Date)
        val primaryGenreName = findViewById<TextView>(R.id.primary_GenreName)
        val country = findViewById<TextView>(R.id.country)
        val trackDuration = findViewById<TextView>(R.id.trackTimeResult)
        val imageUrl = Track.getCoverArtwork(track.artworkUrl100)

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(8))
            .into(trackImage)

        trackName.text = track.trackName
        artistName.text = track.artistName
        collectionName.text = track.collectionName
        releaseDate.text = Track.formatReleaseDate(track.releaseDate)
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
        val trackTimeMillis = try {
            track.trackTimeMillis.toLong()
        } catch (e: NumberFormatException) {
            0L
        }
        trackDuration.text = Track.formatTrackDuration(trackTimeMillis)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
    }
}
