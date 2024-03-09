package com.example.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavoriteBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.sharing.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeViewModel()
        return binding.root
    }

    private fun setupRecyclerView() {

        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            openPlayer(track)
        }
        binding.favoriteTracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackAdapter
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(activity, PlayerActivity::class.java).apply {
            putExtra("track", track)
        }
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteScreenState.FavoritesData -> {
                    if (state.tracks.isEmpty()) {
                        binding.emptyStateImage.visibility = View.VISIBLE
                        binding.emptyStateText.visibility = View.VISIBLE
                    } else {
                        binding.emptyStateImage.visibility = View.GONE
                        binding.emptyStateText.visibility = View.GONE
                        trackAdapter.updateData(state.tracks)
                    }
                }
                else -> {

                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }
}
