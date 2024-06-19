package com.example.ouistici.ui.listeBalises

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.entity.BaliseEntity
import com.example.ouistici.model.ToastUtil
import com.example.ouistici.ui.baliseViewModel.BaliseViewModel
import com.example.ouistici.ui.loader.Loader
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.TableHeaderColor

/**
 * @brief Composable function for rendering a header cell in a table row.
 *
 * @param text The text content of the header cell.
 * @param weight The weight of the header cell in the table layout.
 * @param textColor The color of the text in the header cell.
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
            .height(50.dp)
            .semantics {
                contentDescription =
                    context.getString(R.string.a11y_beaconlisttable_header_title, text)
            },
        color = textColor,
        textAlign = TextAlign.Center
    )
}


/**
 * @brief Composable function for rendering a cell in a table row.
 *
 * @param text The text content of the cell.
 * @param weight The weight of the cell in the table layout.
 * @param textColor The color of the text in the cell.
 * @param onClick Lambda function to handle click events on the cell, if null cell is not clickable.
 */
@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    textColor: Color,
    textSemantics: String,
    onClick: (() -> Unit)?
) {
    onClick?.let {
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(50.dp)
            .clickable(onClick = it)
    }?.let {
        Box(
            it
        ) {
            Text(
                text = text,
                Modifier
                    .align(Alignment.Center)
                    .semantics { contentDescription = "$textSemantics : $text" },
                color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


/**
 * @brief Composable function for rendering a table screen displaying a list of balises.
 *
 * @param balises The list of balises to display in the table.
 * @param navController The NavController used for navigation.
 * @param baliseViewModel The BaliseViewModel for managing balise data.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TableScreenWifi(balises: List<BaliseEntity>, navController: NavController, baliseViewModel: BaliseViewModel) {
    val columnWeight = .5f
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) } // Loader state
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedBalise by remember { mutableStateOf<BaliseEntity?>(null) }


    if (showEditDialog && selectedBalise != null) {
        EditBaliseDialog(
            balise = selectedBalise!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, ip, port ->
                // Update the balise with the new values
                selectedBalise?.let {
                    it.nom = name
                    it.ipBal = "http://$ip:$port/"
                    baliseViewModel.updateBalise(it)
                }
            }
        )
    }

    if (showDeleteDialog && selectedBalise != null) {
        ConfirmDeleteBalisePopup(
            balise = selectedBalise!!,
            navController = navController,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                selectedBalise?.let {
                    baliseViewModel.deleteBalise(it)
                    ToastUtil.showToast(context,
                        context.getString(R.string.toast_beaconlisttable_beacon_deleted))
                }
                showDeleteDialog = false
            }
        )
    }


    LazyColumn(
        Modifier
            .fillMaxSize()
            .height(230.dp)
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(TableHeaderColor)) {
                TableHeaderCell(text = stringResource(R.string.nom_balise), weight = columnWeight, textColor = Color.Black)
                TableHeaderCell(text = "Actions", weight = columnWeight, textColor = Color.Black)
            }
        }



        items(balises) { balise ->
            Row(Modifier.fillMaxWidth()) {
                TableCell(
                    text = balise.nom,
                    weight = columnWeight,
                    textColor = Color.Black,
                    textSemantics = context.getString(R.string.a11y_beaconlisttable_beacon_name),
                    onClick = {
                        isLoading = true
                        baliseViewModel.loadBaliseInfo(balise) { loadedBalise ->
                            if (loadedBalise != null) {
                                baliseViewModel.selectedBalise = loadedBalise
                                navController.navigate("infosBalise")
                            } else {
                                Log.d("Oui", "Problème")
                                ToastUtil.showToast(context,
                                    context.getString(R.string.toast_beaconlisttable_failure_connexion_to_beacon))
                            }
                            isLoading = false // Hide loader
                        }
                    }
                )

                // New cell for edit and delete buttons
                Row(
                    Modifier.weight(columnWeight),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedBalise = balise
                            showEditDialog = true
                        },
                        shape = RectangleShape,
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = BodyBackground
                        ),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .height(66.dp)
                            .weight(columnWeight / 2)
                            .border(1.dp, Color.Black)
                            .semantics {
                                contentDescription =
                                    context.getString(
                                        R.string.a11y_beaconlisttable_modify_beacon,
                                        balise.nom
                                    )
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "",
                            modifier = Modifier.size(15.dp)
                        )
                    }


                    OutlinedButton(
                        onClick = {
                            selectedBalise = balise
                            showDeleteDialog = true
                        },
                        shape = RectangleShape,
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = BodyBackground
                        ),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .height(66.dp)
                            .weight(columnWeight / 2)
                            .border(1.dp, Color.Black)
                            .semantics {
                                contentDescription = context.getString(
                                    R.string.a11y_beaconlisttable_delete_beacon,
                                    balise.nom
                                )
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
            Loader(isLoading = isLoading)
        }
    }
}





@Composable
fun TableScreenBluetooth(balises: List<BaliseEntity>, navController: NavController, baliseViewModel: BaliseViewModel) {
    val columnWeight = .3f
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) } // Loader state

    LazyColumn(
        Modifier
            .fillMaxSize()
            .height(230.dp)
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(TableHeaderColor)) {
                TableHeaderCell(text = stringResource(R.string.nom_balise), weight = columnWeight, textColor = Color.Black)
                TableHeaderCell(text = stringResource(R.string.lieu), weight = columnWeight, textColor = Color.Black)
                TableHeaderCell(text = stringResource(R.string.message_d_faut), weight = columnWeight, textColor = Color.Black)
            }
        }

        items(balises) { balise ->
            Row(Modifier.fillMaxWidth()) {
                // Intégrer les cellules avec TableCell pour les balises bluetooth



                Loader(isLoading = isLoading)
            }
        }
    }
}