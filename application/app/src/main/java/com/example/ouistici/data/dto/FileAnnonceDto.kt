package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName
import java.io.File

class FileAnnonceDto (
    @SerializedName("code") val  code : Int?,
    @SerializedName("value") val  value : String,
    @SerializedName("audiofile") val  audiofile : File
)