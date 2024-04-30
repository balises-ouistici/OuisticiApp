package com.example.ouistici.model

class Balise(
    var nom : String,
    var lieu : String,
    var defaultMessage : String?,
    var annonces : Annonce,
    var volume : Long,
    var plage : List<PlageHoraire>
) {

}