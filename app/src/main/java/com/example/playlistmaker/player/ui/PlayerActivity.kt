package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.Track
import com.example.playlistmaker.player.domain.PlayerViewModel
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.player.data.MediaPlayerManagerImpl

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity() {

    private val mediaPlayerManager = MediaPlayerManagerImpl()

    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(mediaPlayerManager)
    }


    private lateinit var btnPlayPause: ImageButton
    private lateinit var progressTime: TextView
    private lateinit var handler: Handler
    private val updateSeekBar = Runnable { startTrackingTime() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupListeners()
        observeViewModel()
        val track = intent.getSerializableExtra("track") as? Track
        track?.let {
            viewModel.loadTrack(it)
        }

    }

    private fun initViews() {
        btnPlayPause = findViewById(R.id.btnPlay)
        Log.d("PlayerActivity", "Initial Play Button State set to Play")
        progressTime = findViewById(R.id.progressTime)
        handler = Handler(Looper.getMainLooper())
    }

    private fun setupListeners() {
        btnPlayPause.setOnClickListener {
            viewModel.playOrPause()
        }

        findViewById<Button>(R.id.btnPlayerBack).setOnClickListener {
            onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.trackData.observe(this) { track ->
            fillTrackData(track)
        }

        viewModel.playStatus.observe(this) { isPlaying ->
            updateButtonImage(isPlaying)
        }
        viewModel.trackPosition.observe(this) { position ->
            updateTrackPosition(position)
        }
    }

    private fun startTrackingTime() {
        val currentPosition = viewModel.getCurrentPosition()
        progressTime.text = formatTrackDuration(currentPosition)
        if (viewModel.isPlaying()) {
            handler.postDelayed(updateSeekBar, 300)
        }
    }


    private fun updateButtonImage(isPlaying: Boolean) {
        val imageResource = if (isPlaying) R.drawable.button_pause else R.drawable.button_play
        btnPlayPause.setImageResource(imageResource)
    }

    private fun fillTrackData(track: Track) {
        findViewById<TextView>(R.id.trackNameResult).text = track.trackName
        findViewById<TextView>(R.id.artistNameResult).text = track.artistName
        findViewById<TextView>(R.id.collection_Name).text = track.collectionName
        findViewById<TextView>(R.id.release_Date).text = Track.formatReleaseDate(track.releaseDate)
        findViewById<TextView>(R.id.primary_GenreName).text = track.primaryGenreName
        findViewById<TextView>(R.id.country).text = track.country
        val trackTimeMillis = track.trackTimeMillis.toLongOrNull()
        val formattedDuration = if (trackTimeMillis != null) {
            formatTrackDuration(trackTimeMillis)
        } else {
            "00:00"
        }
        findViewById<TextView>(R.id.trackTimeResult).text = formattedDuration


        findViewById<ImageView>(R.id.track_image).apply {
            Glide.with(this@PlayerActivity)
                .load(Track.getCoverArtwork(track.artworkUrl100))
                .placeholder(R.drawable.placeholder)
                .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)))
                .into(this)
        }
    }


    private fun formatTrackDuration(duration: Long): String {
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateSeekBar)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isPlaying()) {
            startTrackingTime()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBar)
        viewModel.releaseResources()
    }
    private fun updateTrackPosition(position: Long) {
        progressTime.text = formatTrackDuration(position)
    }
}
