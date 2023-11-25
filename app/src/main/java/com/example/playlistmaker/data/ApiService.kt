package com.example.playlistmaker.data

import com.example.playlistmaker.data.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    fun searchTrack(
        @Query("term") searchTerm: String
    ): Call<ApiResponse>
}
