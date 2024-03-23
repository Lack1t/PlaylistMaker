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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        playlistAdapter = PlaylistAdapter()
        binding.rvPlaylists.adapter = playlistAdapter
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.setPlaylists(playlists)
            if (playlists.isEmpty()) {

                binding.imageZeroPlaylist.visibility = View.VISIBLE
                binding.zeroPlaylists.visibility = View.VISIBLE
                binding.rvPlaylists.visibility = View.GONE
            } else {
                binding.imageZeroPlaylist.visibility = View.GONE
                binding.zeroPlaylists.visibility = View.GONE
                binding.rvPlaylists.visibility = View.VISIBLE
            }
        }

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

