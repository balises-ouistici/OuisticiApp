package com.example.ouistici.model

class Balise(
    var nom : String,
    var lieu : String?,
    var defaultMessage : Annonce?,
    var annonces : ArrayList<Annonce>,
    var volume : Long,
    var plage : List<PlageHoraire>?
) {

}