package com.example.playlistmaker.sharing.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    fun searchTrack(
        @Query("term") searchTerm: String
    ): Call<ApiResponse>
}
