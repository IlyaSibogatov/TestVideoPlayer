package com.example.testvideoplayer.api

import com.example.testvideoplayer.api.model.ListData
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/api/backgrounds/?group=video&category_id=1")
    fun getList(): Call<ListData>
}