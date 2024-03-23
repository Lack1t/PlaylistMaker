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
import com.example.playlistmaker.data.db.PlaylistEntity

class PlaylistBottomSheetAdapter(
    private var playlists: List<PlaylistEntity>,
    private val onPlaylistSelected: (PlaylistEntity) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImageView: ImageView = itemView.findViewById(R.id.iv_playlist_cover)
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_playlist_title)
        private val numOfTracksTextView: TextView = itemView.findViewById(R.id.tv_num_of_tracks)

        @SuppressLint("SetTextI18n")
        fun bind(playlist: PlaylistEntity) {
            titleTextView.text = playlist.title
            numOfTracksTextView.text = formatTracksCount(playlist.trackCount, itemView.context)
            loadCoverImage(playlist.coverImagePath)
        }

        private fun loadCoverImage(imagePath: String) {
            Glide.with(itemView.context)
                .load(imagePath)
                .placeholder(R.drawable.placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .transform(RoundedCorners(dpToPx(8f, itemView.context)))
                .into(coverImageView)
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
    fun setPlaylists(newPlaylists: List<PlaylistEntity>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

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
