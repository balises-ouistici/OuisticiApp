package com.example.ouistici.model

import java.io.File

/**
 * @brief Represents an announcement.
 * @param id The unique identifier of the announcement.
 * @param nom The name of the announcement.
 * @param type The type of the announcement.
 * @param audio The audio file associated with the announcement (optional).
 * @param contenu The content of the announcement (optional).
 * @param langue The language of the announcement (optional).
 * @param duree The duration of the announcement in seconds (optional).
 */
class Annonce (
    var id : Int,
    var nom : String,
    var type : TypeAnnonce,
    var audio : File?,
    var contenu : String?,
    var langue : Langue?,
    var duree : Int?
)

