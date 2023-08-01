package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    fun searchTrack(
        @Query("term") searchTerm: String
    ): Call<ApiResponse>
}
