package com.example.playlistmaker.media.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.Playlist

class PlaylistAdapter : RecyclerView.Adapter<PlaylistViewHolder>() {
    private var playlists = emptyList<Playlist>()

    fun setPlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.titleTextView.text = playlist.title
        holder.descriptionTextView.text = formatTracksCount(playlist.trackCount, holder.itemView.context)
        if (!playlist.coverImagePath.isNullOrEmpty()) {
            holder.loadCoverImage(playlist.coverImagePath)
        } else {
            holder.coverImageView.setImageResource(R.drawable.placeholder)
        }
    }

    override fun getItemCount(): Int = playlists.size
    companion object {
        fun formatTracksCount(count: Int, context: Context): String {
            val res = context.resources
            return res.getQuantityString(R.plurals.tracks, count, count)
        }
    }
}
