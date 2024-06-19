package com.example.ouistici.data.dto

import com.google.gson.annotations.SerializedName
import java.io.File


/**
 * Data transfer object representing the result of uploading an announcement audio file.
 *
 * @property code The status code of the operation. Can be null.
 * @property value The result message or description.
 * @property audiofile The uploaded audio file.
 */
class FileAnnonceDto (
    @SerializedName("code") val  code : Int?,
    @SerializedName("value") val  value : String,
    @SerializedName("audiofile") val  audiofile : File
)