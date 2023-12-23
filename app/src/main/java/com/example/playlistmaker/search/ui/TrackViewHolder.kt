package com.example.playlistmaker.search.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeMillis: TextView = itemView.findViewById(R.id.trackTime)
    private val trackImage: ImageView = itemView.findViewById(R.id.trackCoverImageView)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTimeMillis.text = track.getFormattedDuration()
        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .transform(RoundedCorners(dpToPx(6f, itemView.context)))
            .into(trackImage)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}

