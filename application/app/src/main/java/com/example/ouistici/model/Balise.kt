package com.example.ouistici.model

class Balise(
    var nom : String,
    var lieu : String?,
    var defaultMessage : Annonce?,
    var annonces : ArrayList<Annonce>,
    var volume : Long,
    var plages : ArrayList<PlageHoraire>
) {
    fun createId(): Int {
        if ( annonces.isEmpty() ) {
            return 0
        }
       return annonces.maxOf { it.id } + 1
    }
}