package com.example.ouistici.data.service

import com.example.ouistici.data.api.OuisticiApi
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.ui.baliseViewModel.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {
    fun setVolume(baliseData: BaliseDto, onResult: (BaliseDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val volume = JsonObject().apply {
            addProperty("volume", baliseData.volume)
        }
        retrofit.setVolume(volume).enqueue(
            object: Callback<BaliseDto> {
                override fun onFailure(call: Call<BaliseDto>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<BaliseDto>, response: Response<BaliseDto>) {
                    val settedVolume = response.body()
                    onResult(settedVolume)
                }
            }
        )
    }

    fun setNameAndPlace(baliseData: BaliseDto, onResult: (BaliseDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val nameAndPlace = JsonObject().apply {
            addProperty("nom", baliseData.nom)
            addProperty("lieu", baliseData.lieu)
        }
        retrofit.setNameAndPlace(nameAndPlace).enqueue(
            object: Callback<BaliseDto> {
                override fun onFailure(call: Call<BaliseDto>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<BaliseDto>, response: Response<BaliseDto>) {
                    val changes = response.body()
                    onResult(changes)
                }
            }
        )
    }



}