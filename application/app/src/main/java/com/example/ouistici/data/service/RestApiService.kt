package com.example.ouistici.data.service

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.ouistici.data.api.OuisticiApi
import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.dto.BaliseInfoDto
import com.example.ouistici.data.dto.FileAnnonceDto
import com.example.ouistici.data.dto.TimeslotDto
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.JoursSemaine
import com.example.ouistici.model.Langue
import com.example.ouistici.model.LangueManager
import com.example.ouistici.model.PlageHoraire
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.ui.baliseViewModel.RetrofitClient
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime

class RestApiService {
    // Get toutes les infos de la balise
    fun fetchBaliseInfo(onResult: (Balise?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        retrofit.getBaliseInfo().enqueue(object: Callback<BaliseInfoDto> {
            override fun onFailure(call: Call<BaliseInfoDto>, t: Throwable) {
                onResult(null)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<BaliseInfoDto>, response: Response<BaliseInfoDto>) {
                if (response.isSuccessful) {
                    val baliseInfoDto = response.body()
                    if (baliseInfoDto != null) {
                        // Map DTO to Model
                        val annonces = baliseInfoDto.annonces.map { dto ->
                            Annonce(
                                id = dto.id_annonce,
                                nom = dto.nom,
                                type = TypeAnnonce.valueOf(dto.type),
                                audio = null, // Assuming audio files are not provided
                                contenu = dto.contenu,
                                langue = if ( dto.langue == "fr" ) {
                                   Langue("fr","Français")
                                               }
                                else {
                                    Langue("en","English")
                                },
                                duree = dto.duree,
                                filename = dto.filename ?: ""
                            )
                        }

                        val plages = baliseInfoDto.timeslots.map { dto ->
                            PlageHoraire(
                                id_timeslot = dto.id_timeslot ?: 0,
                                nomMessage = annonces.find { it.id == dto.id_annonce }!!,
                                jours = listOfNotNull(
                                    if (dto.monday) JoursSemaine.Lundi else null,
                                    if (dto.tuesday) JoursSemaine.Mardi else null,
                                    if (dto.wednesday) JoursSemaine.Mercredi else null,
                                    if (dto.thursday) JoursSemaine.Jeudi else null,
                                    if (dto.friday) JoursSemaine.Vendredi else null,
                                    if (dto.saturday) JoursSemaine.Samedi else null,
                                    if (dto.sunday) JoursSemaine.Dimanche else null
                                ),
                                heureDebut = LocalTime.parse(dto.time_start),
                                heureFin = LocalTime.parse(dto.time_end)
                            )
                        }

                        val baliseDto = baliseInfoDto.balise
                        val balise = Balise(
                            id = baliseDto.balId!!,
                            nom = baliseDto.nom ?: "",
                            lieu = baliseDto.lieu,
                            defaultMessage = annonces.find { it.id == baliseDto.defaultMessage },
                            annonces = ArrayList(annonces),
                            volume = baliseDto.volume,
                            plages = ArrayList(plages),
                            sysOnOff = baliseDto.sysOnOff,
                            ipBal = ""
                        )

                        onResult(balise)
                    } else {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            }
        })
    }


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
            addProperty("id_message", baliseData.defaultMessage)
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


    fun setButtonState(baliseData: BaliseDto, onResult: (BaliseDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val buttonState = JsonObject().apply {
            addProperty("timeslots", baliseData.sysOnOff)
        }
        retrofit.setButtonState(buttonState).enqueue(
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


    fun modifyAnnonce(annonceData: AnnonceDto, onResult: (AnnonceDto?) -> Unit) {
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
        retrofit.modifyAnnonce(annonce).enqueue(
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


    fun deleteAnnonce(annonceData: AnnonceDto, onResult: (AnnonceDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val annonce = JsonObject().apply {
            addProperty("id_annonce", annonceData.id_annonce)
        }
        retrofit.deleteAnnonce(annonce).enqueue(
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


    // Plages horaires
    fun createTimeslot(timeslotData: TimeslotDto, onResult: (TimeslotDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val timeslot = JsonObject().apply {
            addProperty("id_timeslot", timeslotData.id_timeslot)
            addProperty("id_annonce", timeslotData.id_annonce)
            addProperty("monday", timeslotData.monday)
            addProperty("tuesday", timeslotData.tuesday)
            addProperty("wednesday", timeslotData.wednesday)
            addProperty("thursday", timeslotData.thursday)
            addProperty("friday", timeslotData.friday)
            addProperty("saturday", timeslotData.saturday)
            addProperty("sunday", timeslotData.sunday)
            addProperty("time_start", timeslotData.time_start)
            addProperty("time_end", timeslotData.time_end)

        }
        retrofit.createTimeslot(timeslot).enqueue(
            object: Callback<TimeslotDto> {
                override fun onFailure(call: Call<TimeslotDto>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<TimeslotDto>, response: Response<TimeslotDto>) {
                    val changes = response.body()
                    onResult(changes)
                }
            }
        )
    }


    fun modifyTimeslot(timeslotData: TimeslotDto, onResult: (TimeslotDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val timeslot = JsonObject().apply {
            addProperty("id_timeslot", timeslotData.id_timeslot)
            addProperty("id_annonce", timeslotData.id_annonce)
            addProperty("monday", timeslotData.monday)
            addProperty("tuesday", timeslotData.tuesday)
            addProperty("wednesday", timeslotData.wednesday)
            addProperty("thursday", timeslotData.thursday)
            addProperty("friday", timeslotData.friday)
            addProperty("saturday", timeslotData.saturday)
            addProperty("sunday", timeslotData.sunday)
            addProperty("time_start", timeslotData.time_start)
            addProperty("time_end", timeslotData.time_end)

        }
        retrofit.modifyTimeslot(timeslot).enqueue(
            object: Callback<TimeslotDto> {
                override fun onFailure(call: Call<TimeslotDto>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<TimeslotDto>, response: Response<TimeslotDto>) {
                    val changes = response.body()
                    onResult(changes)
                }
            }
        )
    }


    fun deleteTimeslot(timeslotData: TimeslotDto, onResult: (TimeslotDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val timeslot = JsonObject().apply {
            addProperty("id_timeslot", timeslotData.id_timeslot)
        }
        retrofit.deleteTimeslot(timeslot).enqueue(
            object: Callback<TimeslotDto> {
                override fun onFailure(call: Call<TimeslotDto>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<TimeslotDto>, response: Response<TimeslotDto>) {
                    val changes = response.body()
                    onResult(changes)
                }
            }
        )
    }



}