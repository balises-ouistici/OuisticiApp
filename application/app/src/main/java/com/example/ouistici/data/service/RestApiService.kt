package com.example.ouistici.data.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.ouistici.data.api.OuisticiApi
import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.dto.BaliseInfoDto
import com.example.ouistici.data.dto.FileAnnonceDto
import com.example.ouistici.data.dto.TimeslotDto
import com.example.ouistici.data.entity.BaliseEntity
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.JoursSemaine
import com.example.ouistici.model.Langue
import com.example.ouistici.model.PlageHoraire
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.data.RetrofitClient
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime

/**
 * Handles communication with the Ouistici API using Retrofit for various operations related to a Balise entity.
 */
class RestApiService {
    // Get toutes les infos de la balise

    /**
     * Fetches detailed information about a Balise from the API.
     *
     * @param balise The BaliseEntity object representing the Balise for which information is fetched.
     * @param onResult Callback function with a nullable Balise parameter that will be called with the fetched Balise information or null if an error occurs.
     */
    fun fetchBaliseInfo(balise: BaliseEntity, onResult: (Balise?) -> Unit) {
        RetrofitClient.updateBaseUrl(balise.ipBal)
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        Log.d("test", "Valeur retrofit : ")
        retrofit.getBaliseInfo().enqueue(object: Callback<BaliseInfoDto> {
            override fun onFailure(call: Call<BaliseInfoDto>, t: Throwable) {
                onResult(null)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<BaliseInfoDto>, response: Response<BaliseInfoDto>) {
                if (response.isSuccessful) {
                    val baliseInfoDto = response.body()
                    Log.d("test", "Valeur BaliseInfoDto : " + baliseInfoDto?.balise?.nom)
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
                            autovolume = baliseDto.autovolume,
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

    /**
     * Sets the volume for a Balise through the API.
     *
     * @param baliseData The BaliseDto object containing the volume information to be set.
     * @param onResult Callback function with a nullable BaliseDto parameter that will be called with the updated BaliseDto or null if an error occurs.
     */
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

    /**
     * Sets the name and place for a Balise through the API.
     *
     * @param baliseData The BaliseDto object containing the new name and place information.
     * @param onResult Callback function with a nullable BaliseDto parameter that will be called with the updated BaliseDto or null if an error occurs.
     */
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


    /**
     * Tests the sound functionality of the Balise through the API.
     *
     * @param callback Callback function with an Int parameter representing the HTTP status code of the response.
     */
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

    /**
     * Sets the auto volume state for a Balise through the API.
     *
     * @param baliseData The BaliseDto object containing the auto volume state to be set.
     * @param onResult Callback function with a nullable BaliseDto parameter that will be called with the updated BaliseDto or null if an error occurs.
     */
    fun setAutoVolume(baliseData: BaliseDto, onResult: (BaliseDto?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        val buttonState = JsonObject().apply {
            addProperty("autovolume", baliseData.autovolume)
        }
        retrofit.setAutoVolume(buttonState).enqueue(
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

    /**
     * Downloads audio file associated with a specific announcement ID from the API.
     *
     * @param id_annonce The ID of the announcement whose audio file is to be downloaded.
     * @param callback Callback function with a nullable ByteArray parameter representing the downloaded audio file bytes or null if an error occurs.
     */
    fun downloadAudio(id_annonce: Int, callback: (ByteArray?) -> Unit) {
        val retrofit = RetrofitClient.buildService(OuisticiApi::class.java)
        retrofit.downloadAudio(id_annonce).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val bytes = response.body()?.bytes()
                    callback(bytes)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("La requête a échoué: ${t.message}")
                callback(null)
            }
        })
    }



    // Page choix des annonces

    /**
     * Sets the default message for a Balise through the API.
     *
     * @param baliseData The BaliseDto object containing the ID of the default message to be set.
     * @param onResult Callback function with a nullable BaliseDto parameter that will be called with the updated BaliseDto or null if an error occurs.
     */
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

    /**
     * Sets the button state (timeslots) for a Balise through the API.
     *
     * @param baliseData The BaliseDto object containing the new button state information.
     * @param onResult Callback function with a nullable BaliseDto parameter that will be called with the updated BaliseDto or null if an error occurs.
     */
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

    /**
     * Creates a new announcement through the API.
     *
     * @param annonceData The AnnonceDto object containing the information of the announcement to be created.
     * @param onResult Callback function with a nullable AnnonceDto parameter that will be called with the created AnnonceDto or null if an error occurs.
     */
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


    /**
     * Modifies an existing announcement through the API.
     *
     * @param annonceData The AnnonceDto object containing the updated information of the announcement.
     * @param onResult Callback function with a nullable AnnonceDto parameter that will be called with the modified AnnonceDto or null if an error occurs.
     */
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


    /**
     * Deletes an announcement through the API.
     *
     * @param annonceData The AnnonceDto object containing the ID of the announcement to be deleted.
     * @param onResult Callback function with a nullable AnnonceDto parameter that will be called with the deleted AnnonceDto or null if an error occurs.
     */
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

    /**
     * Uploads an audio file for a new announcement through the API.
     *
     * @param fileAnnonceData The FileAnnonceDto object containing the audio file and its description.
     * @param onResult Callback function with a nullable FileAnnonceDto parameter that will be called with the uploaded FileAnnonceDto or null if an error occurs.
     */
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

    /**
     * Creates a new timeslot (plage horaire) through the API.
     *
     * @param timeslotData The TimeslotDto object containing the information of the timeslot to be created.
     * @param onResult Callback function with a nullable TimeslotDto parameter that will be called with the created TimeslotDto or null if an error occurs.
     */
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


    /**
     * Modifies an existing timeslot (plage horaire) through the API.
     *
     * @param timeslotData The TimeslotDto object containing the updated information of the timeslot.
     * @param onResult Callback function with a nullable TimeslotDto parameter that will be called with the modified TimeslotDto or null if an error occurs.
     */
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


    /**
     * Deletes a timeslot (plage horaire) through the API.
     *
     * @param timeslotData The TimeslotDto object containing the ID of the timeslot to be deleted.
     * @param onResult Callback function with a nullable TimeslotDto parameter that will be called with the deleted TimeslotDto or null if an error occurs.
     */
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