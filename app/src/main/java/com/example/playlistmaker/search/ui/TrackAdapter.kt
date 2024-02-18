package com.example.playlistmaker.search.ui

import TrackDiffCallback
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.Track

class TrackAdapter(
    private var trackList: List<Track>,
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
            itemClickListener(track)
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun updateData(newTrackList: List<Track>) {
        val diffCallback = TrackDiffCallback(this.trackList, newTrackList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.trackList = newTrackList
        diffResult.dispatchUpdatesTo(this)
    }
}
