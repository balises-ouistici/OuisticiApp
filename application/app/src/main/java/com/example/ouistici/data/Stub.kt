package com.example.ouistici.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ouistici.model.Balise

@RequiresApi(Build.VERSION_CODES.O)
object Stub {

    /*
    val lAnnonce by lazy {
        createAnnonce()
    }

    val lPlages by lazy {
        createPlage()
    }
    */

    val bal by lazy {
        createBalises()
    }


    private fun createBalises(): List<Balise> {
        return listOf(
            Balise("Balise1","Accueil",  null, ArrayList(), 50, ArrayList(), true),
            Balise("Balise2", null, null, ArrayList(), 30, ArrayList(), false)
        )
    }

    /*
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPlage() : List<PlageHoraire> {
        return listOf(
            PlageHoraire(lAnnonce.get(0), listOf(JoursSemaine.LUNDI, JoursSemaine.MARDI), LocalTime.of(8,0), LocalTime.of(12,30)),
            PlageHoraire(lAnnonce.get(1), listOf(JoursSemaine.LUNDI, JoursSemaine.MARDI), LocalTime.of(13,30), LocalTime.of(17,0)),
            PlageHoraire(lAnnonce.get(2), listOf(JoursSemaine.SAMEDI, JoursSemaine.DIMANCHE), LocalTime.of(8,0), LocalTime.of(18,0)),
            PlageHoraire(lAnnonce.get(3), listOf(JoursSemaine.LUNDI, JoursSemaine.MARDI, JoursSemaine.MERCREDI, JoursSemaine.JEUDI, JoursSemaine.VENDREDI, JoursSemaine.SAMEDI, JoursSemaine.DIMANCHE), LocalTime.of(15,0), LocalTime.of(21,30))
        )
    }
    */



    /*
    private fun createAnnonce() : ArrayList<Annonce> {
        return arrayListOf(
            Annonce("bonjour", TypeAnnonce.AUDIO, R.raw.bonjour, null, null),
            Annonce("comment", TypeAnnonce.TEXTE, R.raw.comment, "Comment allez-vous ?", Langue("fr","francais")),
            Annonce("aurevoir", TypeAnnonce.AUDIO, R.raw.aurevoir, null, null),
            Annonce("comment2", TypeAnnonce.TEXTE, R.raw.comment, "Comment allez-vous ?", Langue("fr","francais"))
            )
    }
    */




}