package com.example.testvideoplayer.db

import com.example.testvideoplayer.api.ApiService
import com.example.testvideoplayer.api.model.ListData
import retrofit2.Callback
import javax.inject.Inject

class   Repository @Inject constructor (private val apiService: ApiService) {
    fun getList(callback: Callback<ListData>) {
        apiService.getList().enqueue(callback)
    }
}