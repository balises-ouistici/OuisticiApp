package com.example.ouistici.ui.infosBalise

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.dto.FileAnnonceDto
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.LangueManager
import com.example.ouistici.model.TextToSpeechManager
import com.example.ouistici.model.ToastUtil
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.ui.annonceTexte.DropdownMenuItemLangue
import com.example.ouistici.ui.annouceAndDaySelector.AnnonceDefaultMessageList
import com.example.ouistici.ui.loader.Loader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

/**
 * @brief Composable function for modifying beacon information via a popup.
 *
 * @param balise The Balise object representing the beacon.
 * @param onDismiss Callback function to dismiss the popup.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModifyInfosBalisePopup(
    balise : Balise,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var selectedAnnonce: Annonce? by remember { mutableStateOf(balise.defaultMessage) }
    var isLoading by remember { mutableStateOf(false) }

    var nomBalise by remember { mutableStateOf(balise.nom) }
    var lieuBalise by remember { mutableStateOf(balise.lieu ?: "") }

    // Contenu du popup
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.informations_balise),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.nom_balise_info_popup),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics { contentDescription = "Nom de la balise. Défiler vers la droite pour accéder à la zone de modification de texte." }
                )
                TextField(
                    value = nomBalise,
                    onValueChange = { nomBalise = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.lieu_info_popup),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics { contentDescription = "Lieu de la balise. Défiler vers la droite pour accéder à la zone de modification de texte." }
                )
                TextField(
                    value = lieuBalise,
                    onValueChange = { lieuBalise = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Choisir annonce par défaut :",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics { contentDescription = "Liste des annonces en défilant sur la droite. Vous pouvez en sélectionner une pour qu'elle soit par défaut sur la balise." }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AnnonceDefaultMessageList(
                    annonces = balise.annonces,
                    selectedAnnonce = selectedAnnonce,
                    onAnnonceSelected = { selectedAnnonce = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = stringResource(R.string.annuler),
                            color = Color.White,
                            modifier = Modifier.semantics { contentDescription = "Annuler les modifications." }
                        )
                    }
                    Button(
                        onClick = {
                            if ( nomBalise != "" ) {
                                isLoading = true
                                val apiService = RestApiService()
                                val balInfo = BaliseDto(
                                    balId = null,
                                    nom = nomBalise,
                                    lieu = lieuBalise,
                                    defaultMessage = selectedAnnonce?.id,
                                    volume = balise.volume,
                                    sysOnOff = balise.sysOnOff,
                                    autovolume = balise.autovolume,
                                    ipBal = balise.ipBal
                                )

                                apiService.setNameAndPlace(balInfo) {
                                    Log.e("InfosBalise","Échec nom/lieu : ${it?.balId}")
                                    if ( it?.balId != null ) {
                                        balise.lieu = lieuBalise
                                        balise.nom = nomBalise
                                        balise.defaultMessage = selectedAnnonce
                                        Log.d("InfosBalise","Nouveau nom et lieu !")
                                        ToastUtil.showToast(context, "Informations modifiées")
                                    } else {
                                        Log.e("InfosBalise","Échec nom/lieu")
                                        ToastUtil.showToast(context, "Échec lors de l'enregistrement")
                                    }
                                }
                                isLoading = false
                                onDismiss()
                                navController.navigate("infosBalise")
                            } else {
                                ToastUtil.showToast(context, "La balise doit avoir un nom !")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Valider",
                            color = Color.White,
                            modifier = Modifier.semantics { contentDescription = "Valider les modifications faites." }
                        )
                    }
                    Loader(isLoading = isLoading)
                }
            }
        }
    }
}


/**
 * @brief Composable function for modifying text announcements via a popup.
 *
 * @param annonce The Annonce object representing the text announcement.
 * @param navController NavController for navigating between composables.
 * @param onDismiss Callback function to dismiss the popup.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModifyAnnoncesBaliseTextePopup(
    annonce: Annonce,
    balise: Balise,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var nomAnnonce by remember { mutableStateOf(annonce.nom) }
    var contenuAnnonceTexte by remember { mutableStateOf(annonce.contenu ?: "") }

    var langueSelectionnee by remember { mutableStateOf(annonce.langue) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) } // Loader state

    val ttsManager = remember { TextToSpeechManager(context) }


    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.informations_annonce),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.semantics {
                        contentDescription = "Informations de l'annonce texte"
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.nom_annonce),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription = "Nom de l'annonce, défiler à droite pour accéder à la zone de modification de texte."
                    }
                )
                TextField(
                    value = nomAnnonce,
                    onValueChange = { nomAnnonce = it },
                    modifier = Modifier.fillMaxWidth()
                )



                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.contenu),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics { contentDescription = "Contenu de l'annonce, défiler à droite pour accéder à la zone de modification de texte." }

                )
                TextField(
                    value = contenuAnnonceTexte,
                    onValueChange = { contenuAnnonceTexte = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Text(
                        text = stringResource(R.string.langue),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.semantics { contentDescription = "Langue de l'annonce, défiler à droite pour définir la langue du synthétiseur vocal." }

                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .clickable(onClick = { expanded = true })
                    ) {

                        Text(
                            text = langueSelectionnee!!.getLangueName(),
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.semantics { contentDescription = "La langue sélectionné est : ${langueSelectionnee!!.getLangueName()}" }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            LangueManager.languesDisponibles.forEach { langue ->
                                DropdownMenuItemLangue(
                                    langue = langue,
                                    onClick = {
                                        langueSelectionnee = langue
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(
                        onClick = {
                            val locale = when (langueSelectionnee!!.code) {
                                "fr" -> Locale.FRENCH
                                "en" -> Locale.ENGLISH
                                else -> Locale.FRENCH
                            }
                            ttsManager.setLanguage(locale)
                            ttsManager.speak(contenuAnnonceTexte)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.semantics { contentDescription = "Écouter l'audio enregistré" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = ""
                        )
                    }
                    Button(
                        onClick = {
                            ttsManager.stop()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.semantics {
                            contentDescription = "Arrêter la lecture audio"
                        }
                    ) {
                        Text(
                            text = "||"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if ( annonce.duree != null ) {
                    Text(
                        text = stringResource(
                            R.string.dur_e_ms_info,
                            annonce.duree!! / 60,
                            annonce.duree!! % 60
                        ),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.semantics { contentDescription = "La durée de l'annonce est de ${annonce.duree!! / 60} minutes et ${annonce.duree!! % 60} secondes." }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.dur_e_probl_me),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.semantics {
                            contentDescription = "Problème rencontré lors de l'affichage de la durée."
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = stringResource(R.string.annuler),
                            color = Color.White,
                            modifier = Modifier.semantics {
                                contentDescription = "Annuler les modifications en cours"
                            }
                        )
                    }

                    val coroutineScope = rememberCoroutineScope()

                    Button(
                        onClick = {
                            if (nomAnnonce != "" && contenuAnnonceTexte != "") {
                                val locale = when (langueSelectionnee?.code) {
                                    "fr" -> Locale.FRENCH
                                    "en" -> Locale.ENGLISH
                                    else -> Locale.FRENCH
                                }
                                ttsManager.setLanguage(locale)

                                val fileName = balise.createId().toString() + ".wav"
                                val file = File(context.cacheDir, fileName)
                                isLoading = true // Show loader
                                coroutineScope.launch {

                                    withContext(Dispatchers.Default) {
                                        ttsManager.saveToFile(contenuAnnonceTexte, file)
                                    }


                                    // Pour laisser le temps au fichier d'être créé
                                    delay(2000L)

                                    val duration = AndroidAudioPlayer.getAudioDuration(file) / 1000

                                    val apiService = RestApiService()
                                    val annInfo = AnnonceDto(
                                        upload_sound_url = null,
                                        id_annonce = annonce.id,
                                        nom = nomAnnonce,
                                        type = TypeAnnonce.TEXTE.toString(),
                                        contenu = contenuAnnonceTexte,
                                        langue = langueSelectionnee?.code,
                                        duree = duration,
                                        filename = fileName
                                    )

                                    apiService.modifyAnnonce(annInfo) {
                                        if (it?.nom != null) {

                                            val audioInfo = FileAnnonceDto(
                                                code = null,
                                                value = it.nom,
                                                audiofile = file
                                            )
                                            apiService.createAudio(audioInfo) {
                                                Log.e(
                                                    "CreateAnnonce",
                                                    "Échec création d'annonce : $it"
                                                )

                                                if (it?.code != null) {
                                                    annonce.contenu = contenuAnnonceTexte
                                                    annonce.nom = nomAnnonce
                                                    annonce.langue = langueSelectionnee
                                                    annonce.duree = duration
                                                    annonce.audio = file

                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.informations_modifi_es),
                                                        Toast.LENGTH_LONG
                                                    )
                                                        .show()
                                                    onDismiss()
                                                } else {
                                                    Log.e(
                                                        "CreateAnnonce",
                                                        "Échec création d'annonce"
                                                    )
                                                    ToastUtil.showToast(context, "Échec lors de l'envoie du fichier au serveur")
                                                }
                                            }
                                        } else {
                                            Log.e("CreateAnnonce", "Échec création d'annonce")
                                            ToastUtil.showToast(context, "Échec lors de la création de l'annonce")
                                        }
                                    }
                                    isLoading = false // Hide loader
                                    navController.navigate("infosBalise")
                                }
                            } else {
                                if ( nomAnnonce == "" && contenuAnnonceTexte != "" ) {
                                    ToastUtil.showToast(context, "L'annonce doit avoir un nom !")
                                }
                                if ( nomAnnonce != "" && contenuAnnonceTexte == "" ) {
                                    ToastUtil.showToast(context, "L'annonce doit avoir un contenu !")
                                }
                                if ( nomAnnonce == "" && contenuAnnonceTexte == "" ) {
                                    ToastUtil.showToast(context, "L'annonce doit avoir un nom et un contenu !")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Valider",
                            color = Color.White,
                            modifier = Modifier.semantics {
                                contentDescription = "Enregistrer les modifications apportées."
                            }
                        )
                    }
                    Loader(isLoading = isLoading)
                }
            }
        }
    }
}



/**
 * @brief Composable function for modifying audio announcements via a popup.
 *
 * @param annonce The Annonce object representing the audio announcement.
 * @param navController NavController for navigating between composables.
 * @param onDismiss Callback function to dismiss the popup.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModifyAnnoncesBaliseAudioPopup(
    annonce: Annonce,
    balise: Balise,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    var nomAnnonce by remember { mutableStateOf(annonce.nom) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.informations_annonce),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.semantics {
                        contentDescription = "Informations de l'annonce audio"
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.nom_annonce),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription = "Nom de l'annonce, défiler à droite pour accéder à la zone de modification de texte."
                    }
                )
                TextField(
                    value = nomAnnonce,
                    onValueChange = { nomAnnonce = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if ( annonce.duree != null ) {
                    Text(
                        text = stringResource(
                            R.string.dur_e_ms_info,
                            annonce.duree!! / 60,
                            annonce.duree!! % 60
                        ),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.semantics { contentDescription = "La durée de l'annonce est de ${annonce.duree!! / 60} minutes et ${annonce.duree!! % 60} secondes." }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.dur_e_probl_me),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.semantics {
                            contentDescription = "Problème rencontré lors de l'affichage de la durée."
                        }
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = stringResource(R.string.annuler),
                            color = Color.White,
                            modifier = Modifier.semantics {
                                contentDescription = "Annuler les modifications en cours"
                            }
                        )
                    }
                    Button(
                        onClick = {
                            if (nomAnnonce != "") {
                                isLoading = true
                                val apiService = RestApiService()
                                val annInfo = AnnonceDto(
                                    upload_sound_url = null,
                                    id_annonce = annonce.id,
                                    nom = nomAnnonce,
                                    type = TypeAnnonce.AUDIO.toString(),
                                    contenu = "",
                                    langue = "",
                                    duree = annonce.duree,
                                    filename = annonce.id.toString()+".wav"
                                )

                                apiService.modifyAnnonce(annInfo) {
                                    if ( it?.nom != null ) {
                                        annonce.nom = nomAnnonce
                                        ToastUtil.showToast(context, "Informations validées")
                                    } else {
                                        Log.e("CreateAnnonce","Échec création d'annonce")
                                        ToastUtil.showToast(context, "Échec lors de la création de l'annonce")
                                    }
                                }
                                isLoading = false
                                navController.navigate("infosBalise")
                                onDismiss()
                            } else {
                                ToastUtil.showToast(context, "L'annonce doit avoir un nom !")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Valider",
                            color = Color.White,
                            modifier = Modifier.semantics { contentDescription = "Enregistrer les modifications apportées." }
                        )
                    }
                    Loader(isLoading = isLoading)
                }
            }
        }
    }
}


/**
 * @brief Composable function for confirming the deletion of an announcement via a popup.
 *
 * @param balise The Balise object to which the announcement belongs.
 * @param annonce The Annonce object representing the announcement to be deleted.
 * @param navController NavController for navigating between composables.
 * @param onDismiss Callback function to dismiss the popup.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConfirmDeleteAnnoncePopup(
    balise: Balise,
    annonce : Annonce,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.tes_vous_s_r_de_vouloir_supprimer_cette_annonce),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = stringResource(R.string.annuler),
                            color = Color.White,
                            modifier = Modifier.semantics { contentDescription = "Annuler la suppression" }
                        )
                    }
                    Button(
                        onClick = {
                            isLoading = true
                            val apiService = RestApiService()
                            val annInfo = AnnonceDto(
                                upload_sound_url = null,
                                id_annonce = annonce.id,
                                nom = annonce.nom,
                                type = annonce.type.toString(),
                                contenu = annonce.contenu,
                                langue = annonce.langue.toString(),
                                duree = annonce.duree,
                                filename = annonce.filename
                            )
                            apiService.deleteAnnonce(annInfo) {
                                if ( it?.id_annonce != null ) {
                                    if ( balise.defaultMessage == annonce ) {
                                        balise.defaultMessage = null
                                    }
                                    balise.annonces.remove(annonce)
                                    ToastUtil.showToast(context, "Annonce supprimée")
                                } else {
                                    Log.e("DeleteAnnonce","Échec suppression d'annonce")
                                    ToastUtil.showToast(context, "Échec lors de la suppression de l'annonce")
                                }
                            }
                            isLoading = false
                            onDismiss()
                            navController.navigate("infosBalise")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.supprimer),
                            color = Color.White,
                            modifier = Modifier.semantics { contentDescription = "Confirmer la suppression de l'annonce" }
                        )
                    }
                    Loader(isLoading = isLoading)
                }
            }
        }
    }
}