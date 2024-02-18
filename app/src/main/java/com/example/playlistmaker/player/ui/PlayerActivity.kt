package com.example.playlistmaker.player.ui
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity(), CoroutineScope {

    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        job = Job()

        setupListeners()
        observeViewModel()

        (intent.getSerializableExtra("track") as? Track)?.let { track ->
            viewModel.loadTrack(track)
        }
    }



    private fun setupListeners() {
        binding.btnPlay.setOnClickListener {
            viewModel.playOrPause()
        }

        binding.btnPlayerBack.setOnClickListener {
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
            binding.progressTime.text = formatTrackDuration(position)
        }
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

    private fun updateButtonImage(isPlaying: Boolean) {
        binding.btnPlay.setImageResource(if (isPlaying) R.drawable.button_pause else R.drawable.button_play)
    }

    private fun formatTrackDuration(durationInMillis: Long): String {
        val minutes = durationInMillis / 60000
        val seconds = (durationInMillis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isPlaying()) {
            viewModel.startTrackingPosition()
        }
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        viewModel.releaseResources()
    }
}
