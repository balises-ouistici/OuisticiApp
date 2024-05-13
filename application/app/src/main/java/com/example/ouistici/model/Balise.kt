package com.example.ouistici.model

class Balise(
    var nom : String,
    var lieu : String?,
    var defaultMessage : Annonce?,
    var annonces : ArrayList<Annonce>,
    var volume : Long,
    var plage : ArrayList<PlageHoraire>
) {
    fun createId(): Int {
        var idTemp = 0
        if ( annonces.isEmpty() ) {
            return idTemp
        }
        idTemp = annonces.maxOf { it.id } + 1
        return idTemp
    }
}