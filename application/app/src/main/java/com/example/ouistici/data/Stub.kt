package com.example.ouistici.data

import com.example.ouistici.R
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.AnnonceTexte
import com.example.ouistici.model.AnnonceVocal
import com.example.ouistici.model.Balise
import com.example.ouistici.model.Langue
import java.io.File

object Stub {

    private val audioDir = File("app/src/main/res/raw/")

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
            AnnonceVocal("bonjour", getFileFromResource(R.raw.bonjour,"bonjour.m4a")),
            AnnonceVocal("aurevoir", getFileFromResource(R.raw.aurevoir, "aurevoir.m4a"))
        )
    }

    private fun createAnnonceTexte() : List<AnnonceTexte> {
        return listOf(
            AnnonceTexte("comment", "Comment-allez vous ?", Langue("fr","francais"), getFileFromResource(R.raw.comment, "comment.m4a")),
            AnnonceTexte("comment2", "Comment-allez vous ?", Langue("fr","francais"), getFileFromResource(R.raw.comment, "comment.m4a"))
        )
    }


    private fun createAnnonce1() : Annonce {
        return Annonce(annVoc, annTexte)
    }


    private fun getFileFromResource(resourceId: Int, resourceName : String): File {
        val file = File(audioDir, "$resourceName.mp3")
        return file
    }
}