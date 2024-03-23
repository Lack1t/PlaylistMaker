package com.example.playlistmaker.media.ui

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

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val coverImageView: ImageView = view.findViewById(R.id.iv_playlist_cover)
    val titleTextView: TextView = view.findViewById(R.id.tv_playlist_title)
    val descriptionTextView: TextView = view.findViewById(R.id.tv_num_of_tracks)
    fun loadCoverImage(imagePath: String) {
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

