package com.example.ouistici.data

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @brief Provides stub data for testing purposes.
 */
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


    /*
    val bal by lazy {
        createBalises()
    }
    */

    /*
    private fun createBalises(): List<Balise> {
        return listOf(
            Balise(1862,"Balise1","Accueil",  null, ArrayList(), 50f, ArrayList(), true, "http://192.168.30.64:5000/"),
            Balise(1862,"Balise2", "", null, ArrayList(), 30f, ArrayList(), false, "http://192.168.30.64:5000/")
        )
    }
    */

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