package com.example.ouistici.ui.baliseViewModel

import com.example.ouistici.data.api.OuisticiApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val client = OkHttpClient.Builder().build()
    

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://172.20.10.4:5000/") // IP de la balise
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}