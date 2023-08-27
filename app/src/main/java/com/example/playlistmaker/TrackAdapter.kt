package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private var trackList: List<Track>, private val searchHistory: SearchHistory) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
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

    override fun getItemCount() = trackList.size

    fun updateData(newTrackList: List<Track>) {
        trackList = newTrackList
        notifyDataSetChanged()
    }

}
