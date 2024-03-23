package com.example.playlistmaker.data.db


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteTrack::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        }
}
