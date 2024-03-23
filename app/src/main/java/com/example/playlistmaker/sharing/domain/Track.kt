package com.example.playlistmaker.sharing.domain

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
                 var isFavorite: Boolean = false,
                 val previewUrl: String,): Serializable
 {
    companion object {
        fun getCoverArtwork(artworkUrl100: String): String {
            return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        }




        fun formatReleaseDate(date: String): String {
            return date.substring(0, 4)
        }
    }
     fun getFormattedDuration(): String {
         val duration = trackTimeMillis?.toLongOrNull() ?: return "Unknown"

         val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
         val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) -
                 TimeUnit.MINUTES.toSeconds(minutes)
         return String.format("%02d:%02d", minutes, seconds)
     }

 }