package com.example.ouistici.ui.infosBalise

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ouistici.ui.choixAnnonce.AnnonceDefaultMessageList
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TableHeaderColor
import com.example.ouistici.ui.theme.TestButtonColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale


/**
 * @brief Composable function for displaying information about a beacon.
 *
 * @param navController The navigation controller for navigating between composables.
 * @param balise The Balise object representing the beacon.
 * @param player The AndroidAudioPlayer object for playing audio.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InfosBalise(
    navController: NavController,
    balise: Balise,
    player: AndroidAudioPlayer
) {
    val showModifyInfosBalisePopup = remember { mutableStateOf(false) }

    var sliderPosition by remember {
        mutableFloatStateOf(balise.volume)
    }

    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    val view = LocalView.current
    LaunchedEffect(Unit) {
        delay(100) // Add a slight delay to ensure the screen is fully loaded
        focusRequester.requestFocus()
        view.announceForAccessibility("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.informations_balise),
            fontSize = 25.sp,
            color = FontColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics {
                contentDescription = "Page des informations de la balise."
            }
                .focusRequester(focusRequester)
                .focusable()
        )

        // Boite avec nom, lieu, message par défaut
        Row {
            Surface(
                modifier = Modifier
                    .padding(8.dp)
                    .widthIn(max = 280.dp)
                    .height(70.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(2.dp, color = Color.Black)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = stringResource(R.string.nom_balise_info, balise.nom),
                        color = FontColor
                    )

                    if ( balise.lieu == "" ) {
                        Text(
                            text = stringResource(R.string.lieu_non_d_fini),
                            color = FontColor
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.lieu_info, balise.lieu!!),
                            color = FontColor
                        )
                    }

                    if ( balise.defaultMessage == null ) {
                        Text(
                            text = stringResource(R.string.message_d_faut_aucun),
                            color = FontColor
                        )
                    } else {
                        Text(
                            text = stringResource(
                                R.string.message_d_faut_info,
                                balise.defaultMessage!!.nom
                            ),
                            color = FontColor
                        )
                    }
                }
            }
            // Bouton pour modifier le nom de la balise
            OutlinedButton(
                onClick = {
                    showModifyInfosBalisePopup.value = true
                },
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = BodyBackground
                ),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .width(50.dp)
                    .height(75.dp)
                    .padding(vertical = 15.dp)
                    .semantics { contentDescription = "Modification des informations de la balise" }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "",
                    modifier = Modifier.size(20.dp)
                )
            }

            if ( showModifyInfosBalisePopup.value ) {
                ModifyInfosBalisePopup(
                    balise = balise,
                    navController = navController
                ) { showModifyInfosBalisePopup.value = false }
            }
        }



        Text(
            text = stringResource(R.string.liste_des_annonces),
            fontSize = 25.sp,
            style = MaterialTheme.typography.titleLarge,
            color = FontColor
        )
        // Liste de toutes les annonces
        TableScreen(balise = balise, player = player, navController = navController)




        // Boite avec Volume de la balise
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .widthIn(max = 300.dp),
            shape = RoundedCornerShape(10.dp),
            color = BodyBackground,
            border = BorderStroke(2.dp, Color.Black),

            ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.volume_de_la_balise),
                    fontSize = 25.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = FontColor
                )
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Black,
                        activeTrackColor = Color.Black,
                        inactiveTrackColor = Color.White
                    ),
                    valueRange = 0f..100f,
                    modifier = Modifier
                        .semantics { contentDescription = "Régler le volume de la balise" }
                )

                Button(
                    onClick = {
                        val apiService = RestApiService()
                        val balInfo = BaliseDto(
                            balId = null,
                            nom = balise.nom,
                            lieu = balise.lieu,
                            defaultMessage = balise.defaultMessage?.id,
                            volume = sliderPosition,
                            sysOnOff = balise.sysOnOff,
                            ipBal = balise.ipBal
                        )

                        apiService.setVolume(balInfo) {
                            if ( it?.balId != null ) {
                                balise.volume = sliderPosition
                                Log.d("InfosBalise","Nouveau volume !")
                                ToastUtil.showToast(context, "Le volume a été modifié")
                            } else {
                                Log.e("InfosBalise","Échec nouveau volume")
                                ToastUtil.showToast(context, "Échec lors de l'enregistrement du volume")
                            }
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = "Enregistrer volume balise",
                        color = Color.White,
                        modifier = Modifier.semantics { contentDescription = "Enregistrer le volume sur la balise" }
                    )
                }
                Button(
                    onClick = {
                        if ( balise.defaultMessage == null ) {
                            ToastUtil.showToast(context, "Impossible, il n'y a pas d'annonce par défaut")
                        } else {
                            val apiService = RestApiService()
                            apiService.testSound() { statusCode ->
                                Log.e("InfosBalise","test son : ${statusCode}")
                                if (statusCode == 200 ) {
                                    ToastUtil.showToast(context, "Test son réussi !")
                                } else {
                                    ToastUtil.showToast(context, "Échec du test son")
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(TestButtonColor)
                ) {
                    Text(
                        text = "Tester sur la balise",
                        color = Color.White,
                        modifier = Modifier
                            .semantics { contentDescription = "Tester le son sur la balise" }
                    )
                }
            }
        }
    }
}


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
                                val apiService = RestApiService()
                                val balInfo = BaliseDto(
                                    balId = null,
                                    nom = nomBalise,
                                    lieu = lieuBalise,
                                    defaultMessage = selectedAnnonce?.id,
                                    volume = balise.volume,
                                    sysOnOff = balise.sysOnOff,
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
                                coroutineScope.launch {

                                    withContext(Dispatchers.Default) {
                                        ttsManager.saveToFile(contenuAnnonceTexte, file)
                                    }


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
                }
            }
        }
    }
}


/**
 * @brief Composable function for rendering a table cell within a row.
 *
 * @param text The text content to be displayed in the cell.
 * @param weight The weight of the cell within the row.
 * @param textColor The color of the text content.
 */
@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    textColor: Color,
    textSemantics: String
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(30.dp)
            .semantics { contentDescription = "$textSemantics $text" },
        color = textColor,
        textAlign = TextAlign.Center
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TableCellHeader(
    text: String,
    weight: Float,
    textColor: Color,
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(30.dp)
            .semantics { contentDescription = "Titre de colonne du tableau, ${text}." },
        color = textColor,
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TableCellButtons(
    text: String,
    weight: Float,
    textColor: Color,
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(30.dp)
            .semantics {
                this.invisibleToUser()
            },
        color = textColor
    )
}


/**
 * @brief Composable function for rendering a table cell with audio controls within a row.
 *
 * @param audioFile The audio file to be played.
 * @param weight The weight of the cell within the row.
 * @param player The Android audio player used to play the audio file.
 */
@Composable
fun RowScope.TableAudioCell(
    audioFile: File?,
    weight: Float,
    player: AndroidAudioPlayer
) {
    Row(
        Modifier
            .weight(weight)
            .border(1.dp, Color.Black)
            .height(46.dp)
            .padding(8.dp)
    ) {
        Button(
            onClick = {
                player.playFile(audioFile ?: return@Button)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.CenterVertically)
                .semantics { contentDescription = "Écouter l'audio de l'annonce" }
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = ""
            )
        }
        Button(
            onClick = {
                player.stop()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.CenterVertically)
                .semantics {
                    contentDescription = "Arrêter la lecture audio"
                }
        ) {
            Text(
                text = "||"
            )
        }

    }
}


/**
 * @brief Composable function for rendering a table screen with announcements for a beacon.
 *
 * @param balise The beacon containing announcements.
 * @param player The Android audio player used for playing audio announcements.
 * @param navController The navigation controller for managing navigation within the app.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TableScreen(balise : Balise, player: AndroidAudioPlayer, navController: NavController) {
    val column1Weight = .3f
    val column2Weight = .44f
    val column3Weight = .13f

    val context = LocalContext.current

    val showModifyAnnonceTextPopup = remember { mutableStateOf(false) }
    val showModifyAnnonceAudioPopup = remember { mutableStateOf(false) }
    val showConfirmDeletePopup = remember { mutableStateOf(false) }


    val idAnnonceEdit = remember { mutableIntStateOf(0) }



    if (balise.annonces.isEmpty()) {
        Column(
            modifier = Modifier
                .height(190.dp)
        ) {
            Text(
                text = stringResource(R.string.il_n_y_a_pas_d_annonces),
                color = FontColor
            )
        }
        Spacer(modifier = Modifier.padding(12.dp))
    } else {
        LazyColumn(
            Modifier
                .padding(16.dp)
                .height(185.dp)
        ) {
            item {
                Row(Modifier.background(TableHeaderColor)) {
                    TableCellHeader(text = stringResource(R.string.nom), weight = column1Weight, textColor = Color.Black)
                    TableCellHeader(text = stringResource(R.string.contenu_tableau), weight = column2Weight, textColor = Color.Black)
                    TableCellButtons(text= "", weight = .26f, textColor = Color.Black)
                }
            }

            items((balise.annonces as List<Annonce>)) { annonce ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(
                        text = annonce.nom,
                        weight = column1Weight,
                        textColor = Color.Black,
                        textSemantics = "Nom de l'annonce"
                    )

                    if (annonce.type == TypeAnnonce.TEXTE) {
                        annonce.contenu?.let {
                            TableCell(
                                text = it,
                                weight = column2Weight,
                                textColor = Color.Black,
                                textSemantics = "Contenu de l'annonce"
                            )

                            OutlinedButton(
                                onClick = {
                                    idAnnonceEdit.intValue = balise.annonces.indexOf(annonce)
                                    showModifyAnnonceTextPopup.value = true
                                },
                                shape = RectangleShape,
                                border = BorderStroke(1.dp, Color.Black),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.Black,
                                    containerColor = BodyBackground
                                ),
                                contentPadding = PaddingValues(8.dp),
                                modifier = Modifier
                                    .height(45.dp)
                                    .weight(column3Weight)
                                    .border(1.dp, Color.Black)
                                    .semantics { contentDescription = "Modifier le nom et le contenu de l'annonce ${annonce.nom}" }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "",
                                    modifier = Modifier.size(15.dp)
                                )
                            }


                            if (showModifyAnnonceTextPopup.value) {
                                ModifyAnnoncesBaliseTextePopup(
                                    annonce = balise.annonces[idAnnonceEdit.intValue],
                                    navController = navController,
                                    balise = balise
                                ) { showModifyAnnonceTextPopup.value = false }
                            }

                        }
                    }

                    if (annonce.type == TypeAnnonce.AUDIO) {
                        TableAudioCell(
                            audioFile = annonce.audio,
                            weight = column2Weight,
                            player = player
                        )

                        OutlinedButton(
                            onClick = {
                                idAnnonceEdit.intValue=balise.annonces.indexOf(annonce)
                                showModifyAnnonceAudioPopup.value = true
                            },
                            shape = RectangleShape,
                            border = BorderStroke(1.dp, Color.Black),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Black,
                                containerColor = BodyBackground
                            ),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier
                                .height(45.dp)
                                .weight(column3Weight)
                                .border(1.dp, Color.Black)
                                .semantics { contentDescription = "Modifier le nom de l'annonce ${annonce.nom}" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "",
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        if (showModifyAnnonceAudioPopup.value) {
                            ModifyAnnoncesBaliseAudioPopup(
                                annonce = balise.annonces[idAnnonceEdit.intValue],
                                balise = balise,
                                navController = navController
                            ) { showModifyAnnonceAudioPopup.value = false }
                        }

                    }

                    OutlinedButton(
                        onClick = {
                            var verif = false
                            for (i in balise.plages ) {
                                if ( annonce == i.nomMessage ) {
                                    verif = true
                                    ToastUtil.showToast(context, "Annonce non supprimable, elle est utilisée dans une plage horaire !")
                                }
                            }
                            if (!verif) {
                                idAnnonceEdit.intValue=balise.annonces.indexOf(annonce)
                                showConfirmDeletePopup.value = true
                            }
                        },
                        shape = RectangleShape,
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = BodyBackground
                        ),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .height(45.dp)
                            .weight(column3Weight)
                            .border(1.dp, Color.Black)
                            .semantics { contentDescription = "Supprimer l'annonce ${annonce.nom}" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    if (showConfirmDeletePopup.value) {
                        ConfirmDeleteAnnoncePopup(
                            balise,
                            annonce = balise.annonces[idAnnonceEdit.intValue],
                            navController = navController
                        ) { showConfirmDeletePopup.value = false }
                    }

                }
            }
        }
    }
}