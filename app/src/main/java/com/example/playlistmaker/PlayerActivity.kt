package com.example.playlistmaker


import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity() {

    private var isInBackground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = intent.getSerializableExtra("track") as? Track

        if (track != null) {
            fillTrackData(track)

            val backButton = findViewById<Button>(R.id.btnPlayerBack)
            backButton.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isInBackground = true
    }

    override fun onResume() {
        super.onResume()
        if (isInBackground) {
            isInBackground = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isInBackground", isInBackground)
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    private fun fillTrackData(track: Track) {
        val trackImage = findViewById<ImageView>(R.id.track_image)
        val trackName = findViewById<TextView>(R.id.trackNameResult)
        val artistName = findViewById<TextView>(R.id.artistNameResult)
        val collectionName = findViewById<TextView>(R.id.collection_Name)
        val releaseDate = findViewById<TextView>(R.id.release_Date)
        val primaryGenreName = findViewById<TextView>(R.id.primary_GenreName)
        val country = findViewById<TextView>(R.id.country)
        val trackDuration = findViewById<TextView>(R.id.trackTimeResult)
        val imageUrl = Track.getCoverArtwork(track.artworkUrl100)

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(8f)))
            .centerCrop()
            .into(trackImage)

        trackName.text = track.trackName
        artistName.text = track.artistName
        collectionName.text = track.collectionName
        releaseDate.text = Track.formatReleaseDate(track.releaseDate)
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        val trackTimeMillis = try {
            track.trackTimeMillis.toLong()
        } catch (e: NumberFormatException) {
            0L
        }
        trackDuration.text = Track.formatTrackDuration(trackTimeMillis)
    }
}

