package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.Intents.Insert.DATA
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.sharing.domain.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.data.SearchRepository
import com.example.playlistmaker.sharing.data.ApiServiceFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearNothingFound: LinearLayout
    private lateinit var linearNoInternet: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var historySearch: TextView
    private var trackClickRunnable: Runnable? = null
    private lateinit var trackAdapter: TrackAdapter
    private var enteredValue: String = ""
    private val handler = Handler(Looper.getMainLooper())

    private val viewModel: SearchViewModel by lazy {
        val apiService = ApiServiceFactory.createApiService()
        val searchHistory = SearchHistory(getSharedPreferences("search_history", MODE_PRIVATE))
        val repository = SearchRepository(apiService, searchHistory)
        val factory = SearchViewModelFactory(repository)

        ViewModelProvider(this, factory)[SearchViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initializeViews()
        setupRecyclerView()
        setupSearchEditText()
        setupButtons()

        viewModel.tracks.observe(this) { tracks ->
            updateUIWithTracks(tracks)
        }
        viewModel.loadSearchHistory()
        savedInstanceState?.getString(DATA)?.let {
            enteredValue = it
            searchEditText.setText(enteredValue)
            viewModel.searchTracks(enteredValue)
        }
    }

    private fun initializeViews() {
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearSearchButton)
        backButton = findViewById(R.id.back)
        recyclerView = findViewById(R.id.recyclerView)
        linearNothingFound = findViewById(R.id.linear_nothing_found)
        linearNoInternet = findViewById(R.id.linear_no_internet)
        refreshButton = findViewById(R.id.buttonRefresh)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)
        historySearch = findViewById(R.id.historySearch)
    }


    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList()) { track ->

            trackClickRunnable?.let { handler.removeCallbacks(it) }


            trackClickRunnable = Runnable {
                viewModel.addTrackToSearchHistory(track)
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("track", track)
                }
                startActivity(intent)
            }


            handler.postDelayed(trackClickRunnable!!, 200)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter
    }



    private fun setupSearchEditText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enteredValue = s.toString()
                viewModel.searchTracks(enteredValue)
                clearButton.visibility = if (enteredValue.isNotEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchTracks(enteredValue)
                true
            } else false
        }
    }


    private fun setupButtons() {
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            viewModel.loadSearchHistory()
        }

        backButton.setOnClickListener {
            finish()
        }

        refreshButton.setOnClickListener {
            viewModel.searchTracks(enteredValue)
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    private fun updateUIWithTracks(tracks: List<Track>) {
        trackAdapter.updateData(tracks)
        val noTracksFound = tracks.isEmpty()

        if (noTracksFound) {
            viewModel.loadSearchHistory()
        } else {
            recyclerView.visibility = View.VISIBLE
            linearNothingFound.visibility = View.GONE
        }

        progressBar.visibility = View.GONE
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(DATA, enteredValue)
        super.onSaveInstanceState(outState)
    }
    override fun onDestroy() {
        super.onDestroy()
        trackClickRunnable?.let { handler.removeCallbacks(it) }
    }

}
