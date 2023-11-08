package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private lateinit var searchHistory: SearchHistory
    private lateinit var historySearch: TextView
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private lateinit var trackAdapter: TrackAdapter
    private var filteredTrackList: List<Track> = emptyList()
    private var enteredValue: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        performSearch(enteredValue)
    }

    companion object {
        private const val DATA = "DATA"
        private const val DEBOUNCE_DELAY: Long = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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

        searchHistory = SearchHistory(getSharedPreferences("search_history", Context.MODE_PRIVATE))
        trackAdapter = TrackAdapter(filteredTrackList, searchHistory) { track ->
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("track", track)
                }
                startActivity(intent)
            }, DEBOUNCE_DELAY)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        backButton.setOnClickListener {
            finish()
        }

        savedInstanceState?.getString(DATA)?.let {
            enteredValue = it
            searchEditText.setText(enteredValue)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enteredValue = s.toString()
                handler.removeCallbacks(searchRunnable)
                if (enteredValue.isNotBlank()) {
                    clearButton.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    handler.postDelayed(searchRunnable, DEBOUNCE_DELAY)
                } else {
                    clearButton.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(enteredValue)
                true
            } else false
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.visibility = View.GONE
            progressBar.visibility = View.GONE
        }

        refreshButton.setOnClickListener {
            performSearch(enteredValue)
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearSearchHistory()
            trackAdapter.updateData(emptyList())
            recyclerView.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            historySearch.visibility = View.GONE
        }

        if (enteredValue.isBlank()) {
            val history = searchHistory.loadSearchHistory()
            if (history.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                trackAdapter.updateData(history)
                clearHistoryButton.visibility = View.VISIBLE
                historySearch.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                historySearch.visibility = View.GONE
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(DATA, enteredValue)
        super.onSaveInstanceState(outState)
    }

    private fun performSearch(searchText: String) {
        progressBar.visibility = View.VISIBLE
        val apiService = retrofit.create(ApiService::class.java)
        apiService.searchTrack(searchText).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val trackList = response.body()?.results ?: emptyList()
                    if (trackList.isNotEmpty()) {
                        recyclerView.visibility = View.VISIBLE
                        linearNothingFound.visibility = View.GONE
                        trackAdapter.updateData(trackList)
                    } else {
                        recyclerView.visibility = View.GONE
                        linearNothingFound.visibility = View.VISIBLE
                    }
                } else {
                    recyclerView.visibility = View.GONE
                    linearNothingFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                linearNoInternet.visibility = View.VISIBLE
            }
        })
    }
}
