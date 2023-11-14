package com.example.playlistmaker.domain

import java.io.Serializable
import java.util.concurrent.TimeUnit

data class Track(val trackName: String,
                 val artistName: String,
                 val trackTimeMillis: String,
                 val artworkUrl100: String,
                 val trackId: String?,
                 val collectionName: String,
                 val releaseDate: String,
                 val primaryGenreName: String,
                 val country: String,
                 val previewUrl: String,): Serializable
 {
    companion object {
        fun getCoverArtwork(artworkUrl100: String): String {
            return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        }

        fun formatTrackDuration(duration: Long): String {
            val minutes = duration / 60000
            val seconds = (duration % 60000) / 1000
            return "%02d:%02d".format(minutes, seconds)
        }


        fun formatReleaseDate(date: String): String {
            return date.substring(0, 4)
        }
    }
     fun getFormattedDuration(): String {
         val minutes = TimeUnit.MILLISECONDS.toMinutes(this.trackTimeMillis.toLong())
         val seconds = TimeUnit.MILLISECONDS.toSeconds(this.trackTimeMillis.toLong()) -
                 TimeUnit.MINUTES.toSeconds(minutes)
         return String.format("%02d:%02d", minutes, seconds)
     }
}