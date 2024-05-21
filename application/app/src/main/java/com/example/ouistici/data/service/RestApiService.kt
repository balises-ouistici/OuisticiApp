package com.example.ouistici.data.service

import android.widget.Toast
import com.example.ouistici.data.api.OuisticiApi
import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.dto.FileAnnonceDto
import com.example.ouistici.ui.baliseViewModel.RetrofitClient
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {
    // Page infos balise
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


    fun testSound(callback : (Int) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        retrofit.testSound().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                callback(response.code())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("La requête a échoué: ${t.message}")
                callback(-1)
            }
        })
    }





    // Page choix des annonces
    fun setDefaultMessage(baliseData: BaliseDto, onResult: (BaliseDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val defaultMessage = JsonObject().apply {
            addProperty("id_message", baliseData.defaultMessage)
        }
        retrofit.setDefaultMessage(defaultMessage).enqueue(
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


    // Création d'annonce
    fun createAnnonce(annonceData: AnnonceDto, onResult: (AnnonceDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val annonce = JsonObject().apply {
            addProperty("id_annonce", annonceData.id_annonce)
            addProperty("nom", annonceData.nom)
            addProperty("type", annonceData.type)
            addProperty("contenu", annonceData.contenu)
            addProperty("lang", annonceData.langue)
            addProperty("duree", annonceData.duree)
            addProperty("filename", annonceData.filename)

        }
        retrofit.createAnnonce(annonce).enqueue(
            object: Callback<AnnonceDto> {
                override fun onFailure(call: Call<AnnonceDto>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<AnnonceDto>, response: Response<AnnonceDto>) {
                    val changes = response.body()
                    onResult(changes)
                }
            }
        )
    }


    fun createAudio(fileAnnonceData: FileAnnonceDto, onResult: (FileAnnonceDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)

        // Create RequestBody instance from file
        val audioFile = fileAnnonceData.audiofile
        val requestFile = RequestBody.create(MediaType.parse("audio/*"), audioFile)
        val body = MultipartBody.Part.createFormData("audiofile", audioFile.name, requestFile)

        // Create description part
        val description = RequestBody.create(MultipartBody.FORM, fileAnnonceData.value)

        retrofit.createAudio(description, body).enqueue(
            object: Callback<FileAnnonceDto> {
                override fun onFailure(call: Call<FileAnnonceDto>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<FileAnnonceDto>, response: Response<FileAnnonceDto>) {
                    val changes = response.body()
                    onResult(changes)
                }
            }
        )
    }




}