package com.example.ouistici.ui.choixAnnonce

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.model.Balise
import com.example.ouistici.model.JoursSemaine
import com.example.ouistici.model.PlageHoraire
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.TableHeaderColor
import java.util.Locale

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
    val context = LocalContext.current
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(30.dp)
            .semantics {
                contentDescription = context.getString(R.string.a11y_timeslottable_header, text)
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
                contentDescription =
                    context.getString(
                        R.string.a11y_timeslottable_days_cell,
                        textSemantics,
                        if (jours.count() == 7) context.getString(R.string.a11y_timeslottable_all_days) else jours
                    )
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
                    textSemantics = context.getString(R.string.a11y_timeslottable_announce_name)
                )

                TableJoursCell(
                    jours = plage.jours,
                    weight = columnJours,
                    textColor = Color.Black,
                    textSemantics = context.getString(R.string.a11y_timeslottable_selected_days)
                )

                TableCells(
                    text = context.getString(R.string.de_a, plage.heureDebut, plage.heureFin),
                    weight = column2Weight,
                    textColor = Color.Black,
                    textSemantics = context.getString(R.string.a11y_timeslottable_activation_time)
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
                            contentDescription =
                                context.getString(R.string.a11y_timeslottable_timeslot_modification)
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
                            contentDescription =
                                context.getString(R.string.a11u_timeslottable_timeslot_deletion)
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