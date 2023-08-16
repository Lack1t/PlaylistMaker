package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private lateinit var trackAdapter: TrackAdapter
    private var filteredTrackList: List<Track> = emptyList()
    private var enteredValue: String = ""

    companion object {
        private const val DATA = "DATA"
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

        backButton.setOnClickListener {
            finish()
        }

        trackAdapter = TrackAdapter(filteredTrackList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        if (savedInstanceState != null) {
            enteredValue = savedInstanceState.getString(DATA, "")
            searchEditText.setText(enteredValue)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) {
                    clearButton.visibility = View.GONE
                    refreshButton.visibility = View.GONE
                    filteredTrackList = emptyList()
                    trackAdapter.updateData(filteredTrackList)
                    recyclerView.visibility = View.GONE
                    linearNothingFound.visibility = View.GONE
                    linearNoInternet.visibility = View.GONE
                } else {
                    clearButton.visibility = View.VISIBLE
                    refreshButton.visibility = View.VISIBLE
                }
                enteredValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(enteredValue)
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                true
            } else {
                false
            }
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        refreshButton.setOnClickListener {
            performSearch(enteredValue)
        }

        if (searchEditText.text.isNotBlank()) {
            performSearch(enteredValue)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(DATA, enteredValue)
    }

    private fun performSearch(searchText: String) {
        val apiService = retrofit.create(ApiService::class.java)

        if (searchText.isBlank()) {
            clearButton.visibility = View.GONE
            refreshButton.visibility = View.GONE
            filteredTrackList = emptyList()
            trackAdapter.updateData(filteredTrackList)
            recyclerView.visibility = View.GONE
            linearNothingFound.visibility = View.GONE
            linearNoInternet.visibility = View.GONE
        } else {
            clearButton.visibility = View.VISIBLE
            apiService.searchTrack(searchText).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val trackList = response.body()?.results ?: emptyList()
                        if (trackList.isNotEmpty()) {
                            recyclerView.visibility = View.VISIBLE
                            linearNothingFound.visibility = View.GONE
                            linearNoInternet.visibility = View.GONE
                            trackAdapter.updateData(trackList)
                        } else {
                            recyclerView.visibility = View.GONE
                            linearNothingFound.visibility = View.VISIBLE
                            linearNoInternet.visibility = View.GONE
                        }
                    } else {
                        recyclerView.visibility = View.GONE
                        linearNothingFound.visibility = View.GONE
                        linearNoInternet.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    recyclerView.visibility = View.GONE
                    linearNothingFound.visibility = View.GONE
                    linearNoInternet.visibility = View.VISIBLE
                }
            })
        }
    }
}