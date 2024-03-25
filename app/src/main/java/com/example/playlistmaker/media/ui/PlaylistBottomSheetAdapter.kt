package com.example.playlistmaker.media.ui
import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.Playlist

class PlaylistBottomSheetAdapter(private var playlists: List<Playlist>,
                                 private val onPlaylistSelected: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetAdapter.PlaylistViewHolder>()  {

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImageView: ImageView = itemView.findViewById(R.id.iv_playlist_cover)
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_playlist_title)
        private val numOfTracksTextView: TextView = itemView.findViewById(R.id.tv_num_of_tracks)

        @SuppressLint("SetTextI18n")
        fun bind(playlist: Playlist) {
            titleTextView.text = playlist.title
            numOfTracksTextView.text = formatTracksCount(playlist.trackCount, itemView.context)
            loadCoverImage(playlist.coverImagePath)
        }

        private fun loadCoverImage(imagePath: String?) {
            if (imagePath != null && imagePath.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(imagePath)
                .placeholder(R.drawable.placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .transform(RoundedCorners(dpToPx(8f, itemView.context)))
                .into(coverImageView)
        } else {
            coverImageView.setImageResource(R.drawable.placeholder)
        }
    }

        private fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_bottomsheet, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onPlaylistSelected(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size

    @SuppressLint("NotifyDataSetChanged")
    fun setPlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    companion object {
        fun formatTracksCount(count: Int, context: Context): String {
            val res = context.resources
            return res.getQuantityString(R.plurals.tracks, count, count)
        }
    }
}
