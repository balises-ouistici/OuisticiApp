package com.example.ouistici.ui.baliseViewModel

import com.example.ouistici.data.api.OuisticiApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var client = OkHttpClient.Builder().build()

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://172.20.10.4:5000/") // Default IP and port
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }

    fun updateBaseUrl(baseUrl: String) {
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}