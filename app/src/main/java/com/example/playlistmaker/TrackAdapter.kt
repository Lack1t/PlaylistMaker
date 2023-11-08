package com.example.playlistmaker

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private var trackList: List<Track>,
    private val searchHistory: SearchHistory,
    private val itemClickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private val clickHandler = Handler(Looper.getMainLooper())
    private val debounceDelay: Long = 300

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            clickHandler.removeCallbacksAndMessages(null)
            clickHandler.postDelayed({
                itemClickListener(track)
                addToSearchHistory(track)
            }, debounceDelay)
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun updateData(newTrackList: List<Track>) {
        trackList = newTrackList
        notifyDataSetChanged()
    }

    private fun addToSearchHistory(track: Track) {
        val history = searchHistory.loadSearchHistory().toMutableList()

        if (history.size >= searchHistory.maxHistorySize) {
            history.removeAt(history.size - 1)
        }

        val existingIndex = history.indexOfFirst { it.trackId == track.trackId }
        if (existingIndex != -1) {
            history.removeAt(existingIndex)
        }

        history.add(0, track)
        searchHistory.saveSearchHistory(history)
    }
}


