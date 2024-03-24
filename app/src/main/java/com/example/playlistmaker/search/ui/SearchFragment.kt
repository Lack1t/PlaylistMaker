package com.example.playlistmaker.search.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.sharing.domain.Track
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("DEPRECATION")
class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter
    private val searchFlow = MutableStateFlow("")
    private val isSearching = MutableStateFlow(false)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchEditText()
        setupButtons()
        observeViewModel()
        viewModel.loadSearchHistory()
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.addTrackToSearchHistory(track)
            openPlayer(track)
        }
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = trackAdapter
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchEditText() {
        binding?.searchEditText?.doOnTextChanged { text, _, _, _ ->
            searchFlow.value = text.toString()
            if (text != null) {
                isSearching.value = text.isNotEmpty()
            }
        }

        lifecycleScope.launchWhenStarted {
            searchFlow.debounce(300).collect { searchText ->
                if (searchText.isEmpty()) {
                    viewModel.loadSearchHistory()
                } else {
                    viewModel.searchTracks(searchText)
                }
                binding?.clearSearchButton?.isVisible = searchText.isNotEmpty()
            }
        }
    }

    private fun setupButtons() {
        binding?.clearSearchButton?.setOnClickListener {
            binding?.searchEditText?.text?.clear()
            hideKeyboard()
            viewModel.loadSearchHistory()
        }

        binding?.clearHistoryButton?.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.tracks.collect { tracks ->
                updateUIWithTracks(tracks)
                binding?.clearHistoryButton?.isVisible = viewModel.isSearchHistoryAvailable.value && !isSearching.value
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isSearchHistoryAvailable.collect { isAvailable ->
                binding?.clearHistoryButton?.isVisible = isAvailable && !isSearching.value
            }
        }
    }


    private fun updateUIWithTracks(tracks: List<Track>) {
        trackAdapter.updateData(tracks)
        val noTracksFound = tracks.isEmpty()
        binding?.linearNothingFound?.isVisible = noTracksFound && searchFlow.value.isNotEmpty()
        binding?.recyclerView?.isVisible = !noTracksFound
    }


    private fun openPlayer(track: Track) {
        val intent = Intent(activity, PlayerActivity::class.java).apply {
            putExtra("track", track)
        }
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(binding?.searchEditText?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
