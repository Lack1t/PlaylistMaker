package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.util.concurrent.TimeUnit

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeMillis: TextView = itemView.findViewById(R.id.trackTime)
    private val trackImage: ImageView = itemView.findViewById(R.id.trackCoverImageView)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName

        val trackTimeMillisValue = track.trackTimeMillis
        if (trackTimeMillisValue != null) {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(trackTimeMillisValue.toLong())
            val seconds = TimeUnit.MILLISECONDS.toSeconds(trackTimeMillisValue.toLong()) -
                    TimeUnit.MINUTES.toSeconds(minutes)

            val timeString = String.format("%02d:%02d", minutes, seconds)
            trackTimeMillis.text = timeString
        } else {
            trackTimeMillis.text = ""
        }

        val radiusDp = 6f
        val radiusPx = dpToPx(radiusDp, itemView.context)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .transform(RoundedCorners(radiusPx))
            .into(trackImage)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
