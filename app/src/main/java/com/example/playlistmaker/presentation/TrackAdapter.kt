package com.example.playlistmaker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.domain.Track

class TrackAdapter(
    private var trackList: List<Track>,
    private val searchHistory: SearchHistory,
    private val handler: Handler,
    private val itemClickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {

            handler.removeCallbacksAndMessages(null)

            handler.postDelayed({
                itemClickListener(track)
                addToSearchHistory(track)
            }, DEBOUNCE_DELAY)
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

        if (!history.any { it.trackId == track.trackId }) {
            if (history.size >= searchHistory.maxHistorySize) {
                history.removeAt(history.size - 1)
            }
            history.add(0, track)
            searchHistory.saveSearchHistory(history)
        }
    }

    companion object {
        private const val DEBOUNCE_DELAY: Long = 500
    }
}
