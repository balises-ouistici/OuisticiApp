package com.example.ouistici.ui.choixAnnonce

import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.dto.TimeslotDto
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.JoursSemaine
import com.example.ouistici.model.PlageHoraire
import com.example.ouistici.model.ToastUtil
import com.example.ouistici.ui.annouceAndDaySelector.AnnonceDefaultMessageList
import com.example.ouistici.ui.annouceAndDaySelector.AnnonceList
import com.example.ouistici.ui.annouceAndDaySelector.JoursSemaineSelector
import com.example.ouistici.ui.loader.Loader
import java.time.LocalTime
import java.util.Calendar

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
    var isLoading by remember { mutableStateOf(false) }
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
                                        contentDescription =
                                            context.getString(R.string.a11y_timeslotpopup_create_announce_button)
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
                                    contentDescription =
                                        context.getString(R.string.a11y_timeslotpopup_cancel_defaultmessage)
                                }
                            )
                        }
                        Button(
                            onClick = {
                                isLoading = true
                                val apiService = RestApiService()
                                val balInfo = BaliseDto(
                                    balId = null,
                                    nom = balise.nom,
                                    lieu = balise.lieu,
                                    defaultMessage = selectedAnnonce?.id,
                                    volume = balise.volume,
                                    sysOnOff = balise.sysOnOff,
                                    autovolume = balise.autovolume,
                                    ipBal = balise.ipBal
                                )

                                apiService.setDefaultMessage(balInfo) {
                                    if (it?.balId != null) {
                                        balise.defaultMessage = selectedAnnonce
                                        Log.d("InfosBalise", "Nouvelle annonce par défaut !")
                                        ToastUtil.showToast(context, context.getString(R.string.nouvelle_annonce_par_d_faut))
                                    } else {
                                        Log.e("InfosBalise", "Échec nouvelle annonce par défaut")
                                        ToastUtil.showToast(context,
                                            context.getString(R.string.toast_timeslotpopup_failure_defaultmessage))
                                    }
                                }
                                isLoading = false
                                onDismiss()
                                navController.navigate("manageAnnonce")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.text_timeslotpopup_validate_button),
                                color = Color.White,
                                modifier = Modifier.semantics {
                                    contentDescription = context.getString(
                                        R.string.a11y_timeslotpopup_validate_button,
                                        selectedAnnonce?.nom
                                    )
                                }
                            )
                        }
                        Loader(isLoading = isLoading)
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
    var isLoading by remember { mutableStateOf(false) }


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
                        contentDescription =
                            context.getString(R.string.a11y_timeslotpopup_add_timeslot)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.choisir_annonce),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription =
                            context.getString(R.string.a11y_timeslotpopup_chose_announce_for_timeslot)
                    }
                )
                if ( balise.annonces.isEmpty() )  {
                    Text(
                        text = stringResource(R.string.cr_ez_d_abord_une_annonce),
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.a11y_timeslotpopup_must_create_announce_for_timeslot)
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
                        contentDescription =
                            context.getString(R.string.a11y_timeslotpopup_chose_activation_days)
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
                        contentDescription =
                            context.getString(R.string.a11y_timeslotpopup_chose_periods)
                    }
                )


                if ( heureDebut == null ) {
                    Text(
                        text = stringResource(R.string.heure_d_but_aucune),
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.a11y_timeslotpopup_beggining_hour)
                        }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.heure_d_but_objet, heureDebut!!),
                        modifier = Modifier.semantics {
                            contentDescription = context.getString(
                                R.string.a11y_timeslotpopup_beggining_time,
                                heureDebut!!.hour,
                                heureDebut!!.minute
                            )
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
                            contentDescription =
                                context.getString(R.string.a11y_timeslotpopup_beggining_hour_button)
                        }
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))


                if ( heureFin == null ) {
                    Text(
                        text = stringResource(R.string.heure_fin_aucune),
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.a11y_timeslotpopup_ending_hour)
                        }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.heure_fin_objet, heureFin!!),
                        modifier = Modifier.semantics {
                            contentDescription = context.getString(
                                R.string.a11y_timeslotpopup_ending_time,
                                heureFin!!.hour,
                                heureFin!!.minute
                            )
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
                            contentDescription =
                                context.getString(R.string.a11y_timeslotpopup_ending_hour_button)
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
                                contentDescription =
                                    context.getString(R.string.a11y_timeslotpopup_cancel_creation_timeslot)
                            }
                        )
                    }
                    Button(
                        onClick = {
                            // Création de la plage horaire
                            if (selectedAnnonce != null && selectedJours.isNotEmpty() && heureDebut != null && heureFin != null) {
                                if ( heureDebut!! >= heureFin!! ) {
                                    ToastUtil.showToast(context,
                                        context.getString(R.string.toast_timeslotpopup_endinghour_error_beggininghour))
                                } else {
                                    isLoading = true
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
                                            ToastUtil.showToast(context,
                                                context.getString(R.string.toast_timeslotpopup_new_timeslot))
                                        } else {
                                            Log.e("InfosBalise", "Échec nouvelle plage horaire")
                                            ToastUtil.showToast(context,
                                                context.getString(R.string.toast_timeslotpopup_failure_saving_timeslot))
                                        }
                                    }
                                    isLoading = false
                                    onDismiss()
                                    navController.navigate("manageAnnonce")
                                }
                            } else {
                                ToastUtil.showToast(context,
                                    context.getString(R.string.toast_timeslotpopup_missing_informations))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Valider",
                            color = Color.White,
                            modifier = Modifier.semantics {
                                contentDescription =
                                    context.getString(R.string.a11y_timeslotpopup_validate_timeslot_creation)
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
    var isLoading by remember { mutableStateOf(false) }


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
                        contentDescription =
                            context.getString(R.string.a11y_timeslotpopup_modify_timeslot_text)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.choisir_annonce),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics {
                        contentDescription =
                            context.getString(R.string.a11y_timeslotpopup_chose_announce_timeslot)
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
                        contentDescription =
                            context.getString(R.string.a11y_timeslot_chose_activation_day)
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
                        contentDescription =
                            context.getString(R.string.a11y_timeslotpopup_chose_periods)
                    }
                )


                if ( heureDebut == null ) {
                    Text(
                        text = stringResource(R.string.heure_d_but_aucune),
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.a11y_timeslotpopup_beggining_hour)
                        }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.heure_d_but_objet, heureDebut!!),
                        modifier = Modifier.semantics {
                            contentDescription = context.getString(
                                R.string.a11y_timeslotpopup_beggining_time,
                                heureDebut!!.hour,
                                heureDebut!!.minute
                            )
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
                            contentDescription =
                                context.getString(R.string.a11y_timeslotpopup_beggining_hour_button)
                        }
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))


                if ( heureFin == null ) {
                    Text(
                        text = stringResource(R.string.heure_fin_aucune),
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.a11y_timeslotpopup_ending_hour)
                        }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.heure_fin_objet, heureFin!!),
                        modifier = Modifier.semantics {
                            contentDescription = context.getString(
                                R.string.a11y_timeslotpopup_ending_time,
                                heureFin!!.hour,
                                heureFin!!.minute
                            )
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
                            contentDescription =
                                context.getString(R.string.a11y_timeslotpopup_ending_hour_button)
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
                                contentDescription =
                                    context.getString(R.string.a11y_timeslotpopup_cancel_modify_timeslot)
                            }
                        )
                    }
                    Button(
                        onClick = {
                            // Création de la plage horaire
                            if (selectedAnnonce != null && selectedJours.isNotEmpty() && heureDebut != null && heureFin != null) {
                                if ( heureDebut!! >= heureFin!! ) {
                                    ToastUtil.showToast(context,
                                        context.getString(R.string.toast_timeslotpopup_endinghour_error_beggininghour))
                                } else {
                                    isLoading = true
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
                                            ToastUtil.showToast(context,
                                                context.getString(R.string.toast_timeslotpopup_timeslot_modified))
                                        } else {
                                            Log.e("InfosBalise", "Échec modification plage horaire")
                                            ToastUtil.showToast(context,
                                                context.getString(R.string.toast_timeslotpopup_failure_modifying_timeslot))
                                        }
                                    }
                                    isLoading = false
                                    onDismiss()
                                    navController.navigate("manageAnnonce")
                                }
                            } else {
                                ToastUtil.showToast(context,
                                    context.getString(R.string.toast_timeslotpopup_missing_informations))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Valider",
                            color = Color.White,
                            modifier = Modifier.semantics {
                                contentDescription =
                                    context.getString(R.string.a11y_timeslotpopup_validate_modification_timeslot)
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
                                contentDescription =
                                    context.getString(R.string.a11y_timeslotpopup_cancel_delete_timeslot)
                            }
                        )
                    }
                    Button(
                        onClick = {
                            isLoading = true
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
                                    ToastUtil.showToast(context,
                                        context.getString(R.string.toast_timeslotpopup_timeslot_deleted))
                                } else {
                                    Log.e("InfosBalise", "Échec suppression plage horaire")
                                    ToastUtil.showToast(context,
                                        context.getString(R.string.toast_timeslotpopup_delete_timeslot_failure))
                                }
                            }
                            isLoading = false
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
                                contentDescription =
                                    context.getString(R.string.a11y_timeslotpopup_validate_delete_timeslot_button)
                            }
                        )
                    }
                    Loader(isLoading = isLoading)
                }
            }
        }
    }
}