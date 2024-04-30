package com.example.ouistici.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ouistici.R
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.AnnonceTexte
import com.example.ouistici.model.AnnonceVocal
import com.example.ouistici.model.Balise
import com.example.ouistici.model.JoursSemaine
import com.example.ouistici.model.Langue
import com.example.ouistici.model.PlageHoraire
import com.example.ouistici.model.TypeAnnonce
import java.io.File
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
object Stub {

    val annVoc by lazy {
        createAnnonceVocale()
    }

    val annTexte by lazy {
        createAnnonceTexte()
    }

    // val anno : Annonce = createAnnonce1()

    val lAnnonce by lazy {
        createAnnonce()
    }

    val lPlages by lazy {
        createPlage()
    }

    val bal by lazy {
        createBalises()
    }


    private fun createBalises(): List<Balise> {
        return listOf(
            Balise("Balise1","Accueil",  "bonjour", lAnnonce, 50, listOf(lPlages.get(0), lPlages.get(2))),
            Balise("Balise2", "Accueil2", "aurevoir", lAnnonce, 30, listOf(lPlages.get(1), lPlages.get(3)))
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPlage() : List<PlageHoraire> {
        return listOf(
            PlageHoraire(lAnnonce.get(0), listOf(JoursSemaine.LUNDI, JoursSemaine.MARDI), LocalTime.of(8,0), LocalTime.of(12,30)),
            PlageHoraire(lAnnonce.get(1), listOf(JoursSemaine.LUNDI, JoursSemaine.MARDI), LocalTime.of(13,30), LocalTime.of(17,0)),
            PlageHoraire(lAnnonce.get(2), listOf(JoursSemaine.SAMEDI, JoursSemaine.DIMANCHE), LocalTime.of(8,0), LocalTime.of(18,0)),
            PlageHoraire(lAnnonce.get(3), listOf(JoursSemaine.LUNDI, JoursSemaine.MARDI, JoursSemaine.MERCREDI, JoursSemaine.JEUDI, JoursSemaine.VENDREDI, JoursSemaine.SAMEDI, JoursSemaine.DIMANCHE), LocalTime.of(15,0), LocalTime.of(21,30))
        )
    }


    private fun createAnnonceVocale() : List<AnnonceVocal> {
        return listOf(
            AnnonceVocal("bonjour", R.raw.bonjour,),
            AnnonceVocal("aurevoir", R.raw.aurevoir)
        )
    }

    private fun createAnnonceTexte() : List<AnnonceTexte> {
        return listOf(
            AnnonceTexte("comment", "Comment allez-vous ?", Langue("fr","francais"), R.raw.comment),
            AnnonceTexte("comment2", "Comment allez-vous ?", Langue("fr","francais"), R.raw.comment)
        )
    }


    /*
    private fun createAnnonce1() : Annonce {
        return Annonce(annVoc, annTexte)
    }
    */

    private fun createAnnonce() : List<Annonce> {
        return listOf(
            Annonce("bonjour", TypeAnnonce.AUDIO, R.raw.bonjour, null, null),
            Annonce("comment", TypeAnnonce.TEXTE, R.raw.comment, "Comment allez-vous ?", Langue("fr","francais")),
            Annonce("aurevoir", TypeAnnonce.AUDIO, R.raw.aurevoir, null, null),
            Annonce("comment2", TypeAnnonce.TEXTE, R.raw.comment, "Comment allez-vous ?", Langue("fr","francais"))
            )
    }



}