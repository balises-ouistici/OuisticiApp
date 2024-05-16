package com.example.ouistici.ui.baliseViewModel

import com.example.ouistici.data.api.OuisticiApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.30.64:5000/"
    

    val apiService: OuisticiApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(OuisticiApi::class.java)
    }
}