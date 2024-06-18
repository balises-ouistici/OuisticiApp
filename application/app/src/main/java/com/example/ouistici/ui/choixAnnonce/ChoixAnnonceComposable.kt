package com.example.ouistici.ui.choixAnnonce

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.Balise
import com.example.ouistici.model.JoursSemaine
import com.example.ouistici.model.ToastUtil
import com.example.ouistici.ui.loader.Loader
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.FontColor
import kotlinx.coroutines.delay

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
            .verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.choix_des_annonces),
            fontSize = 25.sp,
            color = FontColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .semantics { contentDescription = context.getString(R.string.a11y_timeslot_title) }
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
                                context.getString(R.string.a11y_timeslot_chose_defaultannounce_slide)
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
                               contentDescription =
                                   context.getString(R.string.a11y_timeslot_chose_announce_button)
                           }
                       )
                    }

                    if ( balise.defaultMessage == null ) {
                        Text(
                            text = stringResource(R.string.aucun),
                            color = FontColor,
                            modifier = Modifier.semantics {
                                contentDescription =
                                    context.getString(R.string.a11y_timeslot_no_defaultmessage)
                            }
                        )
                    } else {
                        Text(
                            text = balise.defaultMessage!!.nom, // Afficher le message par défaut de la balise
                            color = FontColor,
                            modifier = Modifier.semantics {
                                contentDescription = context.getString(
                                    R.string.a11y_timeslot_defaultmessage_name,
                                    balise.defaultMessage!!.nom
                                )
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
                            context.getString(R.string.a11y_timeslot_sysonoff_slide)
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
                        contentDescription =
                            context.getString(R.string.a11y_timeslot_add_timeslot_button)
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
 * @brief Composable function for rendering an on/off button.
 *
 * @param balise The Balise object representing the switch state.
 * @param navController The navigation controller for navigating between composables.
 */
@Composable
fun OnOffButton(balise: Balise, navController: NavController) {
    val checkedState = remember { mutableStateOf(balise.sysOnOff) }

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checkedState.value,
            onCheckedChange = { isChecked ->
                isLoading = true
                val apiService = RestApiService()
                val balInfo = BaliseDto(
                    balId = null,
                    nom = balise.nom,
                    lieu = balise.lieu,
                    defaultMessage = balise.defaultMessage?.id,
                    volume = balise.volume,
                    sysOnOff = isChecked,
                    autovolume = balise.autovolume,
                    ipBal = balise.ipBal
                )
                apiService.setButtonState(balInfo) {
                    if ( it?.balId != null ) {
                        checkedState.value = isChecked
                        balise.sysOnOff = checkedState.value
                        navController.navigate("manageAnnonce")
                    } else {
                        Log.e("BoutonOfOff","Échec modification état")
                        ToastUtil.showToast(context,
                            context.getString(R.string.toast_timeslot_failure_modifying_button_state))
                    }
                }
                isLoading = false
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
                contentDescription = context.getString(
                    R.string.a11y_timeslot_sysonoff_state,
                    if (checkedState.value) " activé " else " désactivé."
                )
            }
        )
    }
    Loader(isLoading = isLoading)
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

