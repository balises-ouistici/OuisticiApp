package com.example.ouistici.ui.ajouterAnnonce

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.ui.theme.FontColor

@Composable
fun AjouterAnnonce(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Quel type d'annonce ?",
            fontSize = 25.sp,
            color = FontColor
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Vocale",
            fontSize = 15.sp,
            color = FontColor
        )
        LargeFloatingActionButton(
            onClick = { navController.navigate("annonceVocal") },
            shape = CircleShape,
            containerColor = Color.Black,
            modifier = Modifier.width(80.dp)
                .height(80.dp)

        ) {
            Text(
                text = "GO",
                color = Color.White,
                fontSize = 25.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))


        Text(
            text = "Texte",
            fontSize = 15.sp,
            color = FontColor
        )
        LargeFloatingActionButton(
            onClick = { navController.navigate("annonceTexte") },
            shape = CircleShape,
            containerColor = Color.Black,
            modifier = Modifier.width(80.dp)
                .height(80.dp)

        ) {
            Text(
                text = "GO",
                color = Color.White,
                fontSize = 25.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Charger fichier .mp3",
            fontSize = 15.sp,
            color = FontColor
        )
        LargeFloatingActionButton(
            onClick = { navController.navigate("annonceMptrois") },
            shape = CircleShape,
            containerColor = Color.Black,
            modifier = Modifier.width(80.dp)
                .height(80.dp)

        ) {
            Text(
                text = "GO",
                color = Color.White,
                fontSize = 25.sp
            )
        }

    }
}