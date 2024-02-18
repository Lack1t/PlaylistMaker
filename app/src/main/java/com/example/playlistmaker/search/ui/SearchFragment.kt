package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.sharing.domain.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter
    private var searchJob: Job? = null
    private var enteredValue: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
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

    private fun setupSearchEditText() {
        val searchFlow = MutableStateFlow("")

        binding?.searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchFlow.value = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchFlow.debounce(300).onEach { searchText ->
            enteredValue = searchText
            if (searchText.isEmpty()) {
                viewModel.loadSearchHistory()
            } else {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    viewModel.searchTracks(searchText)
                }
            }
            binding?.clearSearchButton?.visibility =
                if (searchText.isNotEmpty()) View.VISIBLE else View.GONE
            binding?.clearHistoryButton?.visibility =
                if (searchText.isEmpty()) View.VISIBLE else View.GONE
        }.launchIn(lifecycleScope)
    }

    private fun setupButtons() {
        binding?.clearSearchButton?.setOnClickListener {
            binding?.searchEditText?.text?.clear()
            hideKeyboard()
            viewModel.loadSearchHistory()
        }

        binding?.buttonRefresh?.setOnClickListener {
            viewModel.searchTracks(enteredValue)
        }

        binding?.clearHistoryButton?.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding?.searchEditText?.windowToken, 0)
    }

    private fun observeViewModel() {
        viewModel.isSearchHistoryAvailable.observe(viewLifecycleOwner) { isAvailable ->
            binding?.clearHistoryButton?.visibility =
                if (isAvailable) View.VISIBLE else View.GONE
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            updateUIWithTracks(tracks)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateUIWithTracks(tracks: List<Track>) {
        trackAdapter.updateData(tracks)
        val noTracksFound = tracks.isEmpty() && enteredValue.isNotEmpty()
        trackAdapter.notifyDataSetChanged()
        binding?.linearNothingFound?.visibility = if (noTracksFound) View.VISIBLE else View.GONE
        binding?.recyclerView?.visibility = if (noTracksFound) View.GONE else View.VISIBLE
        binding?.progressBar?.visibility = View.GONE
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(requireActivity(), PlayerActivity::class.java).apply {
            putExtra("track", track)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        binding = null
    }
}
