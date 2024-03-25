package com.example.playlistmaker.player.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.media.ui.PlaylistBottomSheetAdapter
import com.example.playlistmaker.sharing.domain.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity(), CoroutineScope {

    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var job: Job
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistBottomSheetAdapter: PlaylistBottomSheetAdapter



    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            Log.d("PlayerActivity", "BottomSheet state onCreate: $state")
        }
        binding.rwPlaylists.layoutManager = LinearLayoutManager(this)
        playlistBottomSheetAdapter = PlaylistBottomSheetAdapter(emptyList()) { selectedPlaylist ->
            viewModel.currentTrack.value?.let { track ->
                if (track.trackId != null) {
                    viewModel.addTrackToPlaylist(selectedPlaylist.id, track.trackId, selectedPlaylist.title)
                } else {
                    Log.e("PlayerActivity", "У трека отсутствует ID")
                }
            } ?: run {
                Log.e("PlayerActivity", "Трек не загружен")
            }
        }

        binding.rwPlaylists.adapter = playlistBottomSheetAdapter

        viewModel.loadPlaylists()

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlayPlay.visibility = if (newState == BottomSheetBehavior.STATE_EXPANDED) View.VISIBLE else View.GONE
                Log.d("PlayerActivity", "BottomSheet state changed: $newState")
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        setupListeners()
        observeViewModel()

        intent.getSerializableExtra("track")?.let { track ->
            if (track is Track) {
                viewModel.loadTrack(track)
            }
        }
    }

    private fun setupListeners() {
        binding.btnPlay.setOnClickListener {
            viewModel.playOrPause()
        }

        binding.btnPlayerBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.btnAdd.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                Log.d("PlayerActivity", "BottomSheet expanded")
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Log.d("PlayerActivity", "BottomSheet hidden")
            }
        }

        binding.overlayPlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        val createPlaylistButton: Button = findViewById(R.id.btn_create_playlist)
        createPlaylistButton.setOnClickListener  {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("navigateToCreatePlaylist", true)
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }


    private fun observeViewModel() {
        viewModel.trackData.observe(this) { track ->
            track?.let { fillTrackData(it) }
        }

        viewModel.playStatus.observe(this) { isPlaying ->
            updateButtonImage(isPlaying)
        }

        viewModel.trackPosition.observe(this) { position ->
            binding.progressTime.text = formatTrackDuration(position)
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            updateFavoriteButtonImage(isFavorite)
        }

        viewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (message != null) {
                if (message.startsWith("Добавлено в плейлист")) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
        viewModel.playlists.observe(this) { playlists ->
            playlistBottomSheetAdapter.setPlaylists(playlists)
        }

    }

    private fun fillTrackData(track: Track) {
        with(binding) {
            trackNameResult.text = track.trackName
            artistNameResult.text = track.artistName
            collectionName.text = track.collectionName
            releaseDate.text = Track.formatReleaseDate(track.releaseDate)
            primaryGenreName.text = track.primaryGenreName
            country.text = track.country

            track.trackTimeMillis.toLongOrNull()?.let {
                trackTimeResult.text = formatTrackDuration(it)
            } ?: run {
                trackTimeResult.text = formatTrackDuration(0)
            }

            Glide.with(this@PlayerActivity)
                .load(Track.getCoverArtwork(track.artworkUrl100))
                .placeholder(R.drawable.placeholder)
                .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)))
                .into(trackImage)
        }
    }

    private fun updateButtonImage(isPlaying: Boolean) {
        binding.btnPlay.setImageResource(if (isPlaying) R.drawable.button_pause else R.drawable.button_play)
    }

    private fun updateFavoriteButtonImage(isFavorite: Boolean) {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        val iconResId = when {
            isFavorite && isNightMode -> R.drawable.button_like_favorite_dark
            isFavorite -> R.drawable.button_like_favorite
            isNightMode -> R.drawable.button__like_dark
            else -> R.drawable.button__like
     }

        binding.btnFavorite.setImageResource(iconResId)
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
