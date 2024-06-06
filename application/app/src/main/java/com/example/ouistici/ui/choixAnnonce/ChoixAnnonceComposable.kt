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
import com.example.ouistici.ui.loader.Loader
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
            modifier = Modifier
                .semantics { contentDescription = "Page de choix des annonces" }
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
                contentDescription = "Le système de plage horaire est ${if (checkedState.value) " activé " else " désactivé."}"
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

