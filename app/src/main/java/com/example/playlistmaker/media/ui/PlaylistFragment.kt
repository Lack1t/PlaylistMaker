package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter()
        binding.rvPlaylists.adapter = playlistAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePlaylists()
        setupNewPlaylistButton()
    }

    private fun observePlaylists() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.setPlaylists(playlists)
            updateUIBasedOnPlaylists(playlists.isEmpty())
        }
    }

    private fun updateUIBasedOnPlaylists(isEmpty: Boolean) {
        binding.imageZeroPlaylist.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.zeroPlaylists.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvPlaylists.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun setupNewPlaylistButton() {
        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_createPlaylistFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }
}

