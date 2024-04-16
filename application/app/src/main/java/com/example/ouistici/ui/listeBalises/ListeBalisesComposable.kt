package com.example.ouistici.ui.listeBalises

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import com.example.ouistici.ui.theme.FontColor

@Composable
fun ListeBalises (navController: NavController) {
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


        Surface(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .fillMaxHeight(),

            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = BorderStroke(2.dp, color = Color.Black)
        ) {
            Column() {

            }
            // En-têtes du tableau
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TableHeader(text = "Nom balise")
                TableHeader(text = "Lieu")
                TableHeader(text = "Message défaut")
            }



            // Liste des balises
            LazyColumn {
                items(balises) { balise ->
                    BaliseRow(balise, navController)
                }
            }
        }
    }
}

@Composable
fun TableHeader(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        color = FontColor,
        modifier = Modifier.padding(8.dp)
            .background(Color.LightGray)
            .border(width = 1.dp, color = Color.Black)
            .padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
fun BaliseRow(balise: Balise, navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .border(width = 1.dp, color = Color.Black)
            .clickable {
                navController.navigate("infosBalise")
            }
    ) {
        balise.nom?.let { TableCell(text = it) }
        balise.lieu?.let { TableCell(text = it) }
        balise.defaultMessage?.let { TableCell(text = it) }
    }
}

@Composable
fun TableCell(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        color = FontColor,
        modifier = Modifier.padding(8.dp)
            .border(width = 1.dp, color = Color.Black)
            .padding(horizontal = 4.dp, vertical = 8.dp)
    )
}