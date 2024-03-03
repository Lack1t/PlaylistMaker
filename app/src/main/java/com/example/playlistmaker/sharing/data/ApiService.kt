package com.example.playlistmaker.sharing.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    suspend fun searchTrack(
        @Query("term") searchTerm: String
    ): ApiResponse
}
