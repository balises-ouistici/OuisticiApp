package com.example.ouistici.ui.choixAnnonce

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.dto.TimeslotDto
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.JoursSemaine
import com.example.ouistici.model.PlageHoraire
import com.example.ouistici.model.ToastUtil
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TableHeaderColor
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

/**
 * @brief Composable function for displaying UI to choose announcements for a given balise.
 * It includes UI elements for selecting default messages, managing the system of time slots,
 * adding new time slots, and displaying existing time slots.
 *
 * @param navController NavController for navigating between composables.
 * @param balise Balise object representing the device beacon.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChoixAnnonce(navController: NavController, balise: Balise) {
    val showAddPlageHorairePopup = remember { mutableStateOf(false) }
    val showDefaultMessagePopup = remember { mutableStateOf(false) }


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
            .verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.choix_des_annonces),
            fontSize = 25.sp,
            color = FontColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { contentDescription = "Page de choix des annonces" }
                .focusRequester(focusRequester)
                .focusable()
        )


        // Boîte son par défaut
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .width(350.dp),
            shape = RoundedCornerShape(10.dp),
            color = BodyBackground,
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .padding(15.dp)
            ) {
                Text(
                    text = stringResource(R.string.choisir_annonce_par_d_faut),
                    color = FontColor,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .semantics {
                            contentDescription =
                                "Choisir l'annonce par défaut de la balise, défiler à droite pour accéder au bouton."
                        }
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { showDefaultMessagePopup.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                       Text(
                           text = stringResource(R.string.choisir),
                           color = Color.White,
                           modifier = Modifier.semantics {
                               contentDescription = "Choisir l'annonce par défaut de la balise"
                           }
                       )
                    }

                    if ( balise.defaultMessage == null ) {
                        Text(
                            text = stringResource(R.string.aucun),
                            color = FontColor,
                            modifier = Modifier.semantics {
                                contentDescription = "Aucune annonce par défaut sélectionnée, si vous voulez en sélectionner une, défiler vers la gauche pour accéder au bouton."
                            }
                        )
                    } else {
                        Text(
                            text = balise.defaultMessage!!.nom, // Afficher le message par défaut de la balise
                            color = FontColor,
                            modifier = Modifier.semantics {
                                contentDescription = "L'annonce par défaut de la balise est ${balise.defaultMessage!!.nom}"
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }



                    if (showDefaultMessagePopup.value) {
                        DefaultMessagePopup(
                            balise = balise,
                            navController = navController,
                        ) { showDefaultMessagePopup.value = false }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row {
            Text(
                text = stringResource(R.string.syst_me_de_plage_horaires),
                color = Color.Black,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .semantics {
                        contentDescription =
                            "Système de plage horaire, glisser vers la droite pour accéder au bouton on/off"
                    }

            )

            OnOffButton(balise, navController)
        }

        Spacer(modifier = Modifier.height(30.dp))

        if ( balise.sysOnOff ) {
            Button(
                onClick = { showAddPlageHorairePopup.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = stringResource(R.string.ajouter_une_plage_horaire),
                    color = Color.White,
                    modifier = Modifier.semantics {
                        contentDescription = "Ajouter une nouvelle plage horaire."
                    }
                )
            }

            if (showAddPlageHorairePopup.value) {
                AddPlageHorairePopup(
                    balise = balise,
                    onPlageHoraireAdded = { plageHoraire ->
                        balise.plages.add(plageHoraire)
                        showAddPlageHorairePopup.value = false
                    },
                    onDismiss = { showAddPlageHorairePopup.value = false },
                    navController = navController
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (balise.sysOnOff) 0f else 0.5f))
            ) {
                TableScreen(balise, navController)
            }
        }
    }
}


/**
 * @brief Composable function for displaying a popup to choose a default message for a Balise.
 * It allows selecting an announcement from the list of available announcements.
 *
 * @param balise Balise object representing the device beacon.
 * @param navController NavController for navigating between composables.
 * @param onDismiss Callback function to be invoked when the popup is dismissed.
 */
@Composable
fun DefaultMessagePopup(
    balise: Balise,
    navController: NavController,
    onDismiss: () -> Unit
) {
    var selectedAnnonce: Annonce? by remember { mutableStateOf(balise.defaultMessage) }

    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(R.string.liste_des_annonces), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                AnnonceDefaultMessageList(
                    annonces = balise.annonces,
                    selectedAnnonce = selectedAnnonce,
                    onAnnonceSelected = { selectedAnnonce = if (it == selectedAnnonce) null else it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if ( balise.annonces.isEmpty() ) {
                        Button(
                            onClick = {
                                navController.navigate("addAnnonce")
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.cr_er_annonce),
                                color = Color.White,
                                modifier = Modifier
                                    .semantics {
                                        contentDescription = "Créer une annonce"
                                    }
                            )
                        }
                    } else {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text(
                                text = stringResource(R.string.annuler),
                                color = Color.White,
                                modifier = Modifier.semantics {
                                    contentDescription = "Annuler l'ajout d'une annonce par défaut."
                                }
                            )
                        }
                        Button(
                            onClick = {
                                val apiService = RestApiService()
                                val balInfo = BaliseDto(
                                    balId = null,
                                    nom = balise.nom,
                                    lieu = balise.lieu,
                                    defaultMessage = selectedAnnonce?.id,
                                    volume = balise.volume,
                                    sysOnOff = balise.sysOnOff,
                                    ipBal = balise.ipBal
                                )

                                apiService.setDefaultMessage(balInfo) {
                                    if (it?.balId != null) {
                                        balise.defaultMessage = selectedAnnonce
                                        Log.d("InfosBalise", "Nouvelle annonce par défaut !")
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.nouvelle_annonce_par_d_faut),
                                            Toast.LENGTH_LONG)
                                            .show()
                                    } else {
                                        Log.e("InfosBalise", "Échec nouvelle annonce par défaut")
                                        ToastUtil.showToast(context, "Échec lors de l'enregistrement de l'annonce par défaut")
                                    }
                                }
                                onDismiss()
                                navController.navigate("manageAnnonce")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Valider",
                                color = Color.White,
                                modifier = Modifier.semantics {
                                    contentDescription = "Définir ${selectedAnnonce?.nom} comme annonce par défaut de la balise"
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}


/**
 * @brief Composable function for displaying a popup to add a time range for a Balise.
 * Allows selecting an announcement, days of the week, and start and end times for the time range.
 *
 * @param balise Balise object representing the device beacon.
 * @param onPlageHoraireAdded Callback function invoked when a time range is added.
 * @param onDismiss Callback function to be invoked when the popup is dismissed.
 * @param navController NavController for navigating between composables.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddPlageHorairePopup(
    balise : Balise,
    onPlageHoraireAdded: (PlageHoraire) -> Unit,
    onDismiss: () -> Unit,
    navController: NavController
) {
    // États pour les champs du formulaire
    var selectedAnnonce by remember { mutableStateOf<Annonce?>(null) }
    var selectedJours by remember { mutableStateOf<List<JoursSemaine>>(emptyList()) }
    var heureDebut by remember { mutableStateOf<LocalTime?>(null) }
    var heureFin by remember { mutableStateOf<LocalTime?>(null) }


    val context = LocalContext.current


    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]


    val mTimeStart = remember { mutableStateOf("") }

    val mTimeEnd = remember { mutableStateOf("") }



    val mTimePickerDialogHeureDebut = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            heureDebut = LocalTime.of(mHour,mMinute)
            mTimeStart.value = "$mHour:$mMinute"
        }, mHour, mMinute, true
    )

    val mTimePickerDialogHeureFin = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            heureFin = LocalTime.of(mHour,mMinute)
            mTimeEnd.value = "$mHour:$mMinute"
        }, mHour, mMinute, true
    )



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
                    text = stringResource(R.string.ajouter_une_plage_horaire),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.semantics {
                        contentDescription = "Ajouter une plage horaire."
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.choisir_annonce),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription = "Choisir annonce, défilez vers la droite pour choisir l'annonce qui sera utilisé pour la plage horaire."
                    }
                )
                if ( balise.annonces.isEmpty() )  {
                    Text(
                        text = stringResource(R.string.cr_ez_d_abord_une_annonce),
                        modifier = Modifier.semantics {
                            contentDescription = "Créez d'abord une annonce, vous n'avez pas d'annonce créée, vous ne pouvez donc pas définir de plage horaire."
                        }
                    )
                    Spacer(modifier = Modifier.height(65.dp))
                } else {
                    AnnonceList(
                        annonces = balise.annonces,
                        selectedAnnonce = selectedAnnonce,
                        onAnnonceSelected = { selectedAnnonce = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = stringResource(R.string.choisir_les_jours_d_activations),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription = "Choisir les jours d'activation, défiler à droite pour choisir les jours d'activation de la plage horaire."
                    }
                )
                JoursSemaineSelector(
                    selectedJours = selectedJours,
                    onJoursSelected = { selectedJours = it }
                )


                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = stringResource(R.string.choisir_les_p_riodes),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription = "Choisir les périodes, défilez à droite pour choisir une heure de début et une heure de fin pour la plage horaire."
                    }
                )


                if ( heureDebut == null ) {
                    Text(
                        text = stringResource(R.string.heure_d_but_aucune),
                        modifier = Modifier.semantics {
                            contentDescription = "Heure de début, aucune. Vous pouvez sélectionner une heure de début en défilant en droite sur le bouton."
                        }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.heure_d_but_objet, heureDebut!!),
                        modifier = Modifier.semantics {
                            contentDescription = "Heure de début, ${heureDebut!!.hour} heures et ${heureDebut!!.minute} minutes."
                        }
                    )
                }
                Button(
                    onClick = { mTimePickerDialogHeureDebut.show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
                ) {
                    Text(
                        text = stringResource(R.string.choisir_heure_d_but),
                        color = Color.White,
                        modifier = Modifier.semantics {
                            contentDescription = "Choisir une heure de début pour la plage horaire."
                        }
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))


                if ( heureFin == null ) {
                    Text(
                        text = stringResource(R.string.heure_fin_aucune),
                        modifier = Modifier.semantics {
                            contentDescription = "Heure de fin, aucune. Vous pouvez sélectionner une heure de fin en défilant en droite sur le bouton."
                        }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.heure_fin_objet, heureFin!!),
                        modifier = Modifier.semantics {
                            contentDescription = "Heure de fin, ${heureFin!!.hour} heures et ${heureFin!!.minute} minutes."
                        }
                    )
                }

                Button(
                    onClick = { mTimePickerDialogHeureFin.show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
                ) {
                    Text(
                        text = stringResource(R.string.choisir_heure_fin),
                        color = Color.White,
                        modifier = Modifier.semantics {
                            contentDescription = "Choisir une heure de fin pour la plage horaire."
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
                                contentDescription = "Annuler la création de la plage horaire."
                            }
                        )
                    }
                    Button(
                        onClick = {
                            // Création de la plage horaire
                            if (selectedAnnonce != null && selectedJours.isNotEmpty() && heureDebut != null && heureFin != null) {
                                if ( heureDebut!! >= heureFin!! ) {
                                    ToastUtil.showToast(context, "L'heure de fin ne peut pas être avant celle du début !")
                                } else {
                                    val apiService = RestApiService()
                                    val timeslotInfo = TimeslotDto(
                                        id_timeslot = balise.createIdTimeslot(),
                                        id_annonce = selectedAnnonce!!.id,
                                        monday = JoursSemaine.Lundi in selectedJours,
                                        tuesday = JoursSemaine.Mardi in selectedJours,
                                        wednesday = JoursSemaine.Mercredi in selectedJours,
                                        thursday = JoursSemaine.Jeudi in selectedJours,
                                        friday = JoursSemaine.Vendredi in selectedJours,
                                        saturday = JoursSemaine.Samedi in selectedJours,
                                        sunday = JoursSemaine.Dimanche in selectedJours,
                                        time_start = heureDebut.toString(),
                                        time_end = heureFin.toString(),
                                    )

                                    apiService.createTimeslot(timeslotInfo) {
                                        if (it?.id_timeslot != null) {
                                            balise.plages.add(PlageHoraire(balise.createIdTimeslot(), selectedAnnonce!!, selectedJours, heureDebut!!, heureFin!!))
                                            Log.d("InfosBalise", "Nouvelle plage horaire !")
                                            ToastUtil.showToast(context, "Nouvelle plage horaire créée !")
                                        } else {
                                            Log.e("InfosBalise", "Échec nouvelle plage horaire")
                                            ToastUtil.showToast(context, "Échec lors de l'enregistrement de la nouvelle plage horaire")
                                        }
                                    }
                                    onDismiss()
                                    navController.navigate("manageAnnonce")
                                }
                            } else {
                                ToastUtil.showToast(context, "Informations manquantes !")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Valider",
                            color = Color.White,
                            modifier = Modifier.semantics {
                                contentDescription = "Valider la création de la plage horaire."
                            }
                        )
                    }
                }
            }
        }
    }
}


/**
 * @brief Composable function for displaying a popup to modify a time range for a Balise.
 * Allows selecting an announcement, days of the week, and start and end times for the time range.
 *
 * @param balise Balise object representing the device beacon.
 * @param navController NavController for navigating between composables.
 * @param onDismiss Callback function to be invoked when the popup is dismissed.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModifyPlageHorairePopup(
    balise : Balise,
    plageHoraire : PlageHoraire? = null,
    navController: NavController,
    onDismiss: () -> Unit
) {
    // États pour les champs du formulaire
    var selectedAnnonce by remember { mutableStateOf(plageHoraire?.nomMessage) }
    var selectedJours by remember { mutableStateOf(plageHoraire?.jours ?: emptyList()) }
    var heureDebut by remember { mutableStateOf(plageHoraire?.heureDebut) }
    var heureFin by remember { mutableStateOf(plageHoraire?.heureFin) }


    val context = LocalContext.current




    val mTimeStart = remember { mutableStateOf("") }

    val mTimeEnd = remember { mutableStateOf("") }


    val mTimePickerDialogHeureDebut = heureDebut?.let {
        TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            heureDebut = LocalTime.of(mHour,mMinute)
            mTimeStart.value = "$mHour:$mMinute"
        }, it.hour, it.minute, true
    )
    }

    val mTimePickerDialogHeureFin = heureFin?.let {
        TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            heureFin = LocalTime.of(mHour,mMinute)
            mTimeEnd.value = "$mHour:$mMinute"
        }, it.hour, it.minute, true
    )
    }



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
                    text = stringResource(R.string.modifier_une_plage_horaire),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.semantics {
                        contentDescription = "Modifier une plage horaire."
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.choisir_annonce),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription = "Choisir annonce, défilez vers la droite pour choisir l'annonce qui sera utilisé pour la plage horaire."
                    }
                )

                AnnonceList(
                    annonces = balise.annonces,
                    selectedAnnonce = selectedAnnonce,
                    onAnnonceSelected = { selectedAnnonce = it }
                )
                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = stringResource(R.string.choisir_les_jours_d_activations),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription = "Choisir les jours d'activation, défiler à droite pour choisir les jours d'activation de la plage horaire."
                    }
                )
                JoursSemaineSelector(
                    selectedJours = selectedJours,
                    onJoursSelected = { selectedJours = it }
                )


                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = stringResource(R.string.choisir_les_p_riodes),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription = "Choisir les périodes, défilez à droite pour choisir une heure de début et une heure de fin pour la plage horaire."
                    }
                )


                if ( heureDebut == null ) {
                    Text(
                        text = stringResource(R.string.heure_d_but_aucune),
                        modifier = Modifier.semantics {
                            contentDescription = "Heure de début, aucune. Vous pouvez sélectionner une heure de début en défilant en droite sur le bouton."
                        }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.heure_d_but_objet, heureDebut!!),
                        modifier = Modifier.semantics {
                            contentDescription = "Heure de début, ${heureDebut!!.hour} heures et ${heureDebut!!.minute} minutes."
                        }
                    )
                }
                Button(
                    onClick = {
                        mTimePickerDialogHeureDebut?.show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
                ) {
                    Text(
                        text = stringResource(R.string.choisir_heure_d_but),
                        color = Color.White,
                        modifier = Modifier.semantics {
                            contentDescription = "Choisir une heure de début pour la plage horaire."
                        }
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))


                if ( heureFin == null ) {
                    Text(
                        text = stringResource(R.string.heure_fin_aucune),
                        modifier = Modifier.semantics {
                            contentDescription = "Heure de fin, aucune. Vous pouvez sélectionner une heure de fin en défilant en droite sur le bouton."
                        }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.heure_fin_objet, heureFin!!),
                        modifier = Modifier.semantics {
                            contentDescription = "Heure de fin, ${heureFin!!.hour} heures et ${heureFin!!.minute} minutes."
                        }
                    )
                }

                Button(
                    onClick = {
                        mTimePickerDialogHeureFin?.show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
                ) {
                    Text(
                        text = stringResource(R.string.choisir_heure_fin), color = Color.White,
                        modifier = Modifier.semantics {
                            contentDescription = "Choisir une heure de fin pour la plage horaire."
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
                                contentDescription = "Annuler la modification de la plage horaire."
                            }
                        )
                    }
                    Button(
                        onClick = {
                            // Création de la plage horaire
                            if (selectedAnnonce != null && selectedJours.isNotEmpty() && heureDebut != null && heureFin != null) {
                                if ( heureDebut!! >= heureFin!! ) {
                                    ToastUtil.showToast(context, "L'heure de fin ne peut pas être avant celle du début !")
                                } else {
                                    val apiService = RestApiService()
                                    val timeslotInfo = TimeslotDto(
                                        id_timeslot = plageHoraire!!.id_timeslot,
                                        id_annonce = selectedAnnonce!!.id,
                                        monday = JoursSemaine.Lundi in selectedJours,
                                        tuesday = JoursSemaine.Mardi in selectedJours,
                                        wednesday = JoursSemaine.Mercredi in selectedJours,
                                        thursday = JoursSemaine.Jeudi in selectedJours,
                                        friday = JoursSemaine.Vendredi in selectedJours,
                                        saturday = JoursSemaine.Samedi in selectedJours,
                                        sunday = JoursSemaine.Dimanche in selectedJours,
                                        time_start = heureDebut.toString(),
                                        time_end = heureFin.toString(),
                                    )

                                    apiService.modifyTimeslot(timeslotInfo) {
                                        if (it?.id_timeslot != null) {
                                            plageHoraire.nomMessage = selectedAnnonce as Annonce
                                            plageHoraire.jours = selectedJours
                                            plageHoraire.heureDebut = heureDebut as LocalTime
                                            plageHoraire.heureFin = heureFin as LocalTime
                                            Log.d("InfosBalise", "Plage horaire modifiée !")
                                            ToastUtil.showToast(context, "La plage horaire a bien été modifiée !")
                                        } else {
                                            Log.e("InfosBalise", "Échec modification plage horaire")
                                            ToastUtil.showToast(context, "Échec lors de la modification de la plage horaire")
                                        }
                                    }
                                    onDismiss()
                                    navController.navigate("manageAnnonce")
                                }
                            } else {
                                ToastUtil.showToast(context, "Informations manquantes")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Valider",
                            color = Color.White,
                            modifier = Modifier.semantics {
                                contentDescription = "Valider la modification de la plage horaire."
                            }
                        )
                    }
                }
            }
        }
    }
}


/**
 * @brief Composable function for displaying a list of announcements.
 *
 * @param annonces List of announcements to display.
 * @param selectedAnnonce Currently selected announcement.
 * @param onAnnonceSelected Callback function invoked when an announcement is selected.
 */
@Composable
fun AnnonceList(
    annonces: List<Annonce>,
    selectedAnnonce: Annonce?,
    onAnnonceSelected: (Annonce) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .height(100.dp)
    ) {
        items(annonces) { annonce ->
            val isSelected = annonce == selectedAnnonce
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAnnonceSelected(annonce) }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = annonce.nom,
                        modifier = Modifier.semantics {
                            contentDescription = "Nom de l'annonce, ${annonce.nom}."
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Green // Change the color as desired
                        )
                    }
                }
            }
        }
    }
}


/**
 * @brief Composable function for displaying a list of messages to select a default message in another component.
 *
 * @param annonces List of default messages to display.
 * @param selectedAnnonce Currently selected default message.
 * @param onAnnonceSelected Callback function invoked when a default message is selected.
 */
@Composable
fun AnnonceDefaultMessageList(
    annonces: List<Annonce>,
    selectedAnnonce: Annonce?,
    onAnnonceSelected: (Annonce?) -> Unit
) {
    if ( annonces.isEmpty() ) {
        Text(
            text = stringResource(R.string.cr_ez_d_abord_une_annonce),
            modifier = Modifier.semantics { contentDescription = "Vous n'avez pas encore créé d'annonce, veuillez en créer une dans un premier temps." }
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .height(200.dp)
        ) {
            items(annonces) { annonce ->
                val isSelected = annonce == selectedAnnonce
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAnnonceSelected(if (isSelected) null else annonce) }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = annonce.nom,
                            modifier = Modifier.semantics { contentDescription = "Nom de l'annonce, ${annonce.nom}." },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Green // Change the color as desired
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * @brief Composable function for selecting days of the week.
 *
 * @param selectedJours Currently selected days of the week.
 * @param onJoursSelected Callback function invoked when days of the week are selected.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun JoursSemaineSelector(
    selectedJours: List<JoursSemaine>,
    onJoursSelected: (List<JoursSemaine>) -> Unit
) {
    val joursSemaine = JoursSemaine.values().toList()

    val context = LocalContext.current
    val resources = context.resources

    Box(
        modifier = Modifier
            .height(200.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .width(300.dp)
        ) {
            for (jour in joursSemaine) {
                val isChecked = selectedJours.contains(jour)
                val onClick = {
                    val updatedList = if (isChecked) {
                        selectedJours - jour
                    } else {
                        selectedJours + jour
                    }
                    onJoursSelected(updatedList)
                }

                val stringResourceId = resources.getIdentifier(jour.name.lowercase(Locale.ROOT), "string", context.packageName)
                val jourName = if (stringResourceId != 0) {
                    resources.getString(stringResourceId)
                } else {
                    jour.name // Utilise le nom de l'enum si la ressource n'est pas trouvée
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onClick() }
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = null,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = jourName, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}


/**
 * @brief Composable function for confirming the deletion of a time slot.
 *
 * @param balise The Balise associated with the time slot.
 * @param plageHoraire The time slot to be deleted.
 * @param navController The NavController for navigation.
 * @param onDismiss Callback function invoked when the popup is dismissed.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConfirmDeletePlagePopup(
    balise: Balise,
    plageHoraire : PlageHoraire,
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
                    text = stringResource(R.string.tes_vous_s_r_de_vouloir_supprimer_cette_plage),
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
                            modifier = Modifier.semantics {
                                contentDescription = "Annuler la suppression de la plage horaire."
                            }
                        )
                    }
                    Button(
                        onClick = {
                            val apiService = RestApiService()
                            val timeslotInfo = TimeslotDto(
                                id_timeslot = plageHoraire.id_timeslot,
                                id_annonce = plageHoraire.nomMessage.id,
                                monday = JoursSemaine.Lundi in plageHoraire.jours,
                                tuesday = JoursSemaine.Mardi in plageHoraire.jours,
                                wednesday = JoursSemaine.Mercredi in plageHoraire.jours,
                                thursday = JoursSemaine.Jeudi in plageHoraire.jours,
                                friday = JoursSemaine.Vendredi in plageHoraire.jours,
                                saturday = JoursSemaine.Samedi in plageHoraire.jours,
                                sunday = JoursSemaine.Dimanche in plageHoraire.jours,
                                time_start = plageHoraire.heureDebut.toString(),
                                time_end = plageHoraire.heureFin.toString(),
                            )

                            apiService.deleteTimeslot(timeslotInfo) {
                                if (it?.id_timeslot != null) {
                                    balise.plages.remove(plageHoraire)
                                    Log.d("InfosBalise", "Plage horaire supprimée !")
                                    ToastUtil.showToast(context, "Plage horaire supprimée")
                                } else {
                                    Log.e("InfosBalise", "Échec suppression plage horaire")
                                    ToastUtil.showToast(context, "Échec lors de la suppression de la plage horaire")
                                }
                            }
                            onDismiss()
                            navController.navigate("manageAnnonce")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.supprimer),
                            color = Color.White,
                            modifier = Modifier.semantics {
                                contentDescription = "Valider la suppression de la plage horaire."
                            }
                        )
                    }
                }
            }
        }
    }
}


/**
 * @brief Composable function for rendering a cell in the table header.
 *
 * @param text The text to be displayed in the cell.
 * @param weight The weight of the cell in the row.
 * @param textColor The color of the text in the cell.
 */
@Composable
fun RowScope.TableHeaderCell(
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
                       contentDescription = "Titre de colonne du tableau, ${text}."
            },
        color = textColor,
        textAlign = TextAlign.Center
    )
}



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TableHeaderCellButtons(
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
 * @brief Composable function for rendering a cell in the table representing days of the week.
 *
 * @param jours The list of days of the week.
 * @param weight The weight of the cell in the row.
 * @param textColor The color of the text in the cell.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun RowScope.TableJoursCell(
    jours: List<JoursSemaine>,
    weight: Float,
    textColor: Color,
    textSemantics: String
) {
    val context = LocalContext.current
    val sortedJours = sortDaysOfWeek(jours)

    val joursSemaineStringList = sortedJours.map { jour ->
        val stringResourceId = context.resources.getIdentifier(jour.name.lowercase(Locale.ROOT), "string", context.packageName)
        if (stringResourceId != 0) {
            // Utilisation de substring pour obtenir les deux premières lettres
            context.getString(stringResourceId).substring(0, 2)
        } else {
            jour.name.take(2) // Utilise les deux premières lettres du nom de l'enum si la ressource n'est pas trouvée
        }
    }

    val textToShow = if (sortedJours.size == 7) {
        // Si tous les jours de la semaine sont sélectionnés, affiche "Tous"
        context.getString(R.string.tous_les_jours)
    } else {
        // Sinon, affiche les deux premières lettres de chaque jour séparées par des virgules
        joursSemaineStringList.joinToString(", ")
    }

    Text(
        text = textToShow,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(50.dp)
            .semantics {
                       contentDescription = "${textSemantics}, ${ if (jours.count() == 7) "Tous les jours" else  jours }"
            },
        color = textColor,
        textAlign = TextAlign.Center
    )
}


/**
 * @brief Composable function for rendering a cell in the table.
 *
 * @param text The text content of the cell.
 * @param weight The weight of the cell in the row.
 * @param textColor The color of the text in the cell.
 */
@Composable
fun RowScope.TableCells(
    text: String,
    weight: Float,
    textColor: Color,
    textSemantics: String
) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(50.dp)

    ) {
        Text(
            text = text,
            Modifier
                .align(Alignment.Center)
                .semantics {
                    contentDescription = "${textSemantics}, ${text}."
                },
            color = textColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}


/**
 * @brief Composable function for rendering the table screen displaying time slots.
 *
 * @param balise The Balise object containing time slots.
 * @param navController The navigation controller for navigating between time slots.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TableScreen(balise : Balise, navController: NavController) {
    val column1Weight = .24f
    val columnJours = .28f
    val column2Weight = .22f
    val column3Weight = .13f

    val context = LocalContext.current

    val showModifyPlagePopup = remember { mutableStateOf(false) }

    val idPlageEdit = remember { mutableIntStateOf(0) }

    val showConfirmDeletePopup = remember { mutableStateOf(false) }



    LazyColumn(
        Modifier
            .fillMaxSize()
            .height(343.dp)
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(TableHeaderColor)) {
                TableHeaderCell(text = stringResource(R.string.nom_mess), weight = column1Weight, textColor = Color.Black)
                TableHeaderCell(text = stringResource(R.string.jours), weight = columnJours, textColor = Color.Black)
                TableHeaderCell(text = stringResource(R.string.horaires), weight = column2Weight, textColor = Color.Black)
                TableHeaderCellButtons(text = "", weight = .26f, textColor = Color.Black)
            }
        }

        // SYSTEME D'AJOUT DES PLAGES HORAIRES QUAND BOUTON APPUYE
        items((balise.plages as List<PlageHoraire>)) { plage ->
            Row(Modifier.fillMaxWidth()) {
                TableCells(
                    text = plage.nomMessage.nom,
                    weight = column1Weight,
                    textColor = Color.Black,
                    textSemantics = "Nom de l'annonce sélectionnée"
                )

                TableJoursCell(
                    jours = plage.jours,
                    weight = columnJours,
                    textColor = Color.Black,
                    textSemantics = "Jours d'activation de la plage horaire"
                )

                TableCells(
                    text = context.getString(R.string.de_a, plage.heureDebut, plage.heureFin),
                    weight = column2Weight,
                    textColor = Color.Black,
                    textSemantics = "Plage horaire active"
                )


                OutlinedButton(
                    onClick = {
                        idPlageEdit.intValue = balise.plages.indexOf(plage)
                        showModifyPlagePopup.value = true
                    },
                    shape = RectangleShape,
                    border = BorderStroke(1.dp, Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = BodyBackground
                    ),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .weight(column3Weight)
                        .height(66.dp)
                        .border(1.dp, Color.Black)
                        .semantics {
                            contentDescription = "Modification de la plage horaire."
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "",
                        modifier = Modifier.size(15.dp)
                    )
                }

                if (showModifyPlagePopup.value) {
                    ModifyPlageHorairePopup(
                        balise = balise,
                        plageHoraire = balise.plages[idPlageEdit.intValue],
                        navController = navController
                    ) { showModifyPlagePopup.value = false }
                }



                OutlinedButton(
                    onClick = {
                        idPlageEdit.intValue = balise.plages.indexOf(plage)
                        showConfirmDeletePopup.value = true
                },
                    shape = RectangleShape,
                    border = BorderStroke(1.dp, Color.Black),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = BodyBackground
                    ),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .weight(column3Weight)
                        .height(66.dp)
                        .border(1.dp, Color.Black)
                        .semantics {
                            contentDescription = "Suppression de la plage horaire."
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier.size(15.dp)
                    )
                }

                if (showConfirmDeletePopup.value) {
                    ConfirmDeletePlagePopup(
                        balise = balise,
                        plageHoraire = balise.plages[idPlageEdit.intValue],
                        navController = navController
                    ) { showConfirmDeletePopup.value = false }
                }

            }
        }

    }
}


/**
 * @brief Composable function for rendering an on/off button.
 *
 * @param balise The Balise object representing the switch state.
 * @param navController The navigation controller for navigating between composables.
 */
@Composable
fun OnOffButton(balise: Balise, navController: NavController) {
    val checkedState = remember { mutableStateOf(balise.sysOnOff) }

    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checkedState.value,
            onCheckedChange = { isChecked ->

                val apiService = RestApiService()
                val balInfo = BaliseDto(
                    balId = null,
                    nom = balise.nom,
                    lieu = balise.lieu,
                    defaultMessage = balise.defaultMessage?.id,
                    volume = balise.volume,
                    sysOnOff = isChecked,
                    ipBal = balise.ipBal
                )
                apiService.setButtonState(balInfo) {
                    if ( it?.balId != null ) {
                        checkedState.value = isChecked
                        balise.sysOnOff = checkedState.value
                        navController.navigate("manageAnnonce")
                    } else {
                        Log.e("BoutonOfOff","Échec modification état")
                        ToastUtil.showToast(context, "Échec lors de la modification de l'état du bouton")
                    }
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.Green,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.Black,
                checkedBorderColor = Color.Green,
                uncheckedBorderColor = Color.Black
            ),
        )
        
        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = if (checkedState.value) "On" else "Off",
            color = Color.Black,
            modifier = Modifier.semantics {
                contentDescription = "Le système de plage horaire est ${if (checkedState.value) " activé " else " désactivé."}"
            }
        )
    }
}


fun sortDaysOfWeek(jours: List<JoursSemaine>): List<JoursSemaine> {
    val daysOrder = listOf(
        JoursSemaine.Lundi,
        JoursSemaine.Mardi,
        JoursSemaine.Mercredi,
        JoursSemaine.Jeudi,
        JoursSemaine.Vendredi,
        JoursSemaine.Samedi,
        JoursSemaine.Dimanche
    )
    return jours.sortedBy { daysOrder.indexOf(it) }
}

