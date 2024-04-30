package com.example.ouistici.model

import java.time.LocalTime


class PlageHoraire(
    var nomMessage : Annonce,
    var jours : List<JoursSemaine>,
    var heureDebut : LocalTime,
    var heureFin : LocalTime
) {

}