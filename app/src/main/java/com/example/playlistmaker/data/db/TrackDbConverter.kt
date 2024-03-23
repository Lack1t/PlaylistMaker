package com.example.playlistmaker.data.db

import com.example.playlistmaker.sharing.domain.Track

class TrackDbConverter {
    companion object {
        fun fromDomainModelToDb(track: Track): FavoriteTrack {
            return FavoriteTrack(
                trackId = track.trackId ?: "",
                trackName = track.trackName,
                artistName = track.artistName,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                trackTimeMillis = track.trackTimeMillis.toLongOrNull() ?: 0L,
                addedTimestamp = System.currentTimeMillis()
            )
        }

        fun fromDbToDomainModel(favoriteTrack: FavoriteTrack): Track {
            return Track(
                trackName = favoriteTrack.trackName,
                artistName = favoriteTrack.artistName,
                trackTimeMillis = favoriteTrack.trackTimeMillis.toString(),
                artworkUrl100 = favoriteTrack.artworkUrl100,
                trackId = favoriteTrack.trackId,
                collectionName = favoriteTrack.collectionName,
                releaseDate = favoriteTrack.releaseDate,
                primaryGenreName = favoriteTrack.primaryGenreName,
                country = favoriteTrack.country,
                previewUrl = favoriteTrack.previewUrl
            )
        }
    }
}
