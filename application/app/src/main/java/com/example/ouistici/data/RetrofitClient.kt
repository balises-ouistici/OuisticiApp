package com.example.ouistici.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object that manages the Retrofit instance and provides methods for updating the base URL and creating API service instances.
 */
object RetrofitClient {
    private var retrofit: Retrofit? = null

    /**
     * Updates the base URL of the Retrofit instance.
     *
     * @param baseUrl The new base URL to update the Retrofit instance.
     */
    fun updateBaseUrl(baseUrl: String) {
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    /**
     * Creates and returns an instance of the specified API service interface using the current Retrofit instance.
     *
     * @param service The Class representing the API service interface.
     * @return An instance of the specified API service interface.
     * @throws IllegalStateException if Retrofit instance is not initialized (base URL not updated).
     */
    fun <T> buildService(service: Class<T>): T {
        return retrofit!!.create(service)
    }
}
