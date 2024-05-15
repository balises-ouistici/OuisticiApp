package com.example.ouistici.ui.listeBalises

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.Stub
import com.example.ouistici.model.Balise
import com.example.ouistici.ui.baliseViewModel.BaliseViewModel
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TableHeaderColor


/**
 * @brief Composable function for rendering a list of beacons.
 *
 * @param navController The navigation controller for managing navigation within the app.
 * @param baliseViewModel The view model for managing beacon data.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListeBalises(navController: NavController, baliseViewModel: BaliseViewModel) {
    val balises by remember { mutableStateOf(Stub.bal) }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.liste_des_balises),
            fontSize = 25.sp,
            color = FontColor
        )

        TableScreen(balises, navController, baliseViewModel)

    }
}


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
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(50.dp),
        color = textColor
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
                .align(Alignment.Center),
            color = textColor
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
@Composable
fun TableScreen(balises : List<Balise>, navController: NavController, baliseViewModel: BaliseViewModel) {
    val columnWeight = .3f
    val context = LocalContext.current
    
    LazyColumn(
        Modifier
            .fillMaxSize()
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
                TableCell(
                    text = balise.nom,
                    weight = columnWeight,
                    textColor = Color.Black,
                    onClick = {
                        baliseViewModel.selectedBalise = balise
                        navController.navigate("infosBalise")
                    }
                )

                if ( balise.lieu == null ) {
                    TableCell(
                        text = context.getString(R.string.non_defini),
                        weight = columnWeight,
                        textColor = Color.Black,
                        onClick = {
                            baliseViewModel.selectedBalise = balise
                            navController.navigate("infosBalise")
                        }
                    )
                } else {
                    TableCell(
                        text = balise.lieu!!,
                        weight = columnWeight,
                        textColor = Color.Black,
                        onClick = {
                            baliseViewModel.selectedBalise = balise
                            navController.navigate("infosBalise")
                        }
                    )
                }


                if ( balise.defaultMessage == null ) {
                    TableCell(
                        text = context.getString(R.string.aucun),
                        weight = columnWeight,
                        textColor = Color.Black,
                        onClick = {
                            baliseViewModel.selectedBalise = balise
                            navController.navigate("infosBalise")
                        }
                    )
                } else {
                    TableCell(
                        text = balise.defaultMessage!!.nom,
                        weight = columnWeight,
                        textColor = Color.Black,
                        onClick = {
                            baliseViewModel.selectedBalise = balise
                            navController.navigate("infosBalise")
                        }
                    )
                }
            }
        }
    }
}
