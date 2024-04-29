package com.example.ouistici.data

import com.example.ouistici.R
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.AnnonceTexte
import com.example.ouistici.model.AnnonceVocal
import com.example.ouistici.model.Balise
import com.example.ouistici.model.Langue

object Stub {

    val annVoc by lazy {
        createAnnonceVocale()
    }

    val annTexte by lazy {
        createAnnonceTexte()
    }

    val anno : Annonce = createAnnonce1()

    val bal by lazy {
        createBalises()
    }


    private fun createBalises(): List<Balise> {
        return listOf(
            Balise("Balise1","Accueil",  "bonjour", anno, 50),
            Balise("Balise2", "Accueil2", "aurevoir", anno, 30)
        )
    }

    private fun createAnnonceVocale() : List<AnnonceVocal> {
        return listOf(
            AnnonceVocal("bonjour", R.raw.bonjour),
            AnnonceVocal("aurevoir", R.raw.aurevoir)
        )
    }

    private fun createAnnonceTexte() : List<AnnonceTexte> {
        return listOf(
            AnnonceTexte("comment", "Comment-allez vous ?", Langue("fr","francais"), R.raw.comment),
            AnnonceTexte("comment2", "Comment-allez vous ?", Langue("fr","francais"), R.raw.comment)
        )
    }


    private fun createAnnonce1() : Annonce {
        return Annonce(annVoc, annTexte)
    }
}