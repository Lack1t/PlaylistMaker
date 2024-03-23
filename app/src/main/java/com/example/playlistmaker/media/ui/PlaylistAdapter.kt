package com.example.playlistmaker.media.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.data.db.PlaylistEntity

class PlaylistAdapter : RecyclerView.Adapter<PlaylistViewHolder>() {
    private var playlists = emptyList<PlaylistEntity>()

    fun setPlaylists(newPlaylists: List<PlaylistEntity>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        val context = holder.itemView.context
        holder.titleTextView.text = playlist.title
        holder.descriptionTextView.text = formatTracksCount(playlist.trackCount, context)
        if (playlist.coverImagePath.isNotEmpty()) {
            holder.loadCoverImage(playlist.coverImagePath)
        } else {
            holder.coverImageView.setImageResource(R.drawable.placeholder)
        }
    }

    override fun getItemCount() = playlists.size

    companion object {
        fun formatTracksCount(count: Int, context: Context): String {
            val res = context.resources
            return when {
                count % 10 == 1 && count % 100 != 11 -> res.getQuantityString(R.plurals.tracks, 1, count)
                count % 10 in 2..4 && !(count % 100 in 12..14) -> res.getQuantityString(R.plurals.tracks, 2, count)
                else -> res.getQuantityString(R.plurals.tracks, count, count)
            }
        }
    }
}

