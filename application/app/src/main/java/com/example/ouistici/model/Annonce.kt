package com.example.ouistici.model

import java.io.File

class Annonce (
    var id : Int,
    var nom : String,
    var type : TypeAnnonce,
    var audio : File?,
    var contenu : String?,
    var langue : Langue?
) {

}