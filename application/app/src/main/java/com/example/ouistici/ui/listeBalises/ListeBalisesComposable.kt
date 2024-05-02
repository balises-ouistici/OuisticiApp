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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.data.Stub
import com.example.ouistici.model.Balise
import com.example.ouistici.ui.baliseViewModel.BaliseViewModel
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TableHeaderColor


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListeBalises(navController: NavController, baliseViewModel: BaliseViewModel) {
    val balises by remember { mutableStateOf(Stub.bal) }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Liste des balises",
            fontSize = 25.sp,
            color = FontColor
        )

        TableScreen(balises, navController, baliseViewModel)

    }
}

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


@Composable
fun TableScreen(balises : List<Balise>, navController: NavController, baliseViewModel: BaliseViewModel) {
    val columnWeight = .3f
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(TableHeaderColor)) {
                TableHeaderCell(text = "Nom balise", weight = columnWeight, textColor = Color.Black)
                TableHeaderCell(text = "Lieu", weight = columnWeight, textColor = Color.Black)
                TableHeaderCell(text = "Message défaut", weight = columnWeight, textColor = Color.Black)

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
                        text = "Non défini",
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
                        text = "Aucun",
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
