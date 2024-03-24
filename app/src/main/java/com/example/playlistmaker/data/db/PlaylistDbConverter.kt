package com.example.playlistmaker.data.db

import com.example.playlistmaker.sharing.domain.Playlist

class PlaylistDbConverter {
    companion object {
        fun PlaylistEntity.toDomainModel(): Playlist = Playlist(
            id = this.id,
            title = this.title,
            description = this.description,
            coverImagePath = this.coverImagePath,
            trackIds = this.trackIds,
            trackCount = this.trackCount
        )

        fun Playlist.toEntity(): PlaylistEntity = PlaylistEntity(
            id = this.id,
            title = this.title,
            description = this.description ?: "",
            coverImagePath = this.coverImagePath ?: "",
            trackIds = this.trackIds,
            trackCount = this.trackCount
        )

    }
}
