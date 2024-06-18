package com.example.ouistici.ui.infosBalise

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.Balise
import com.example.ouistici.model.ToastUtil
import com.example.ouistici.ui.loader.Loader
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TestButtonColor
import kotlinx.coroutines.delay


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
    var isLoading by remember { mutableStateOf(false) } // Loader state

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
            modifier = Modifier
                .semantics {
                    contentDescription = context.getString(R.string.a11y_infos_title)
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
                        color = FontColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if ( balise.lieu == "" ) {
                        Text(
                            text = stringResource(R.string.lieu_non_d_fini),
                            color = FontColor
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.lieu_info, balise.lieu),
                            color = FontColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                            color = FontColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                    .semantics {
                        contentDescription =
                            context.getString(R.string.a11y_infos_modify_beacon_infos_button)
                    }
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
                        .semantics { contentDescription =
                            context.getString(R.string.a11y_infos_set_beacon_volume_slider) }
                )

                Row {
                    Text(
                        text = "Adaptation du volume : ",
                        color = Color.Black,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .semantics {
                                contentDescription =
                                    context.getString(R.string.a11y_infos_volume_adapter)
                            }

                    )

                    AutoVolumeButton(balise = balise, navController = navController)
                }

                Button(
                    onClick = {
                        isLoading = true
                        val apiService = RestApiService()
                        val balInfo = BaliseDto(
                            balId = null,
                            nom = balise.nom,
                            lieu = balise.lieu,
                            defaultMessage = balise.defaultMessage?.id,
                            volume = sliderPosition,
                            sysOnOff = balise.sysOnOff,
                            autovolume = balise.autovolume,
                            ipBal = balise.ipBal
                        )

                        apiService.setVolume(balInfo) {
                            if ( it?.balId != null ) {
                                balise.volume = sliderPosition
                                Log.d("InfosBalise","Nouveau volume !")
                                ToastUtil.showToast(context,
                                    context.getString(R.string.toast_infos_volume_modified))
                            } else {
                                Log.e("InfosBalise","Échec nouveau volume")
                                ToastUtil.showToast(context,
                                    context.getString(R.string.toast_infos_failure_modifying_volume))
                            }
                        }
                        isLoading = false
                    },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = stringResource(R.string.text_infos_save_beacon_volume),
                        color = Color.White,
                        modifier = Modifier.semantics { contentDescription =
                            context.getString(R.string.a11y_infos_save_beacon_volume_button) }
                    )
                }
                Button(
                    onClick = {
                        if ( balise.defaultMessage == null ) {
                            ToastUtil.showToast(context,
                                context.getString(R.string.toast_infos_impossible_play_button))
                        } else {
                            isLoading = true
                            val apiService = RestApiService()
                            apiService.testSound() { statusCode ->
                                Log.e("InfosBalise","test son : ${statusCode}")
                                if (statusCode == 200 ) {
                                    ToastUtil.showToast(context,
                                        context.getString(R.string.toast_infos_testsound_success))
                                } else {
                                    ToastUtil.showToast(context,
                                        context.getString(R.string.toast_infos_testsound_failure))
                                }
                            }
                            isLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(TestButtonColor)
                ) {
                    Text(
                        text = stringResource(R.string.text_infos_test_sound_on_beacon_button),
                        color = Color.White,
                        modifier = Modifier
                            .semantics { contentDescription =
                                context.getString(R.string.a11y_infos_test_sound_on_beacon_button) }
                    )
                }
                Loader(isLoading = isLoading)
            }
        }
    }
}




@Composable
fun AutoVolumeButton(balise: Balise, navController: NavController) {
    val checkedState = remember { mutableStateOf(balise.autovolume) }

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
                    sysOnOff = balise.sysOnOff,
                    autovolume = isChecked,
                    ipBal = balise.ipBal
                )
                apiService.setAutoVolume(balInfo) {
                    if ( it?.balId != null ) {
                        checkedState.value = isChecked
                        balise.autovolume = checkedState.value
                        navController.navigate("infosBalise")
                    } else {
                        Log.e("Infos","Échec modification état")
                        ToastUtil.showToast(context,
                            context.getString(R.string.toast_infos_failure_modify_autovolume))
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
                    R.string.a11y_infos_autovolume_state,
                    if (checkedState.value) " activé " else " désactivé."
                )
            }
        )
    }
    Loader(isLoading = isLoading)
}




