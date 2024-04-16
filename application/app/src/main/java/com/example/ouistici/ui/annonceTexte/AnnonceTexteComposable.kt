package com.example.ouistici.ui.annonceTexte

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.ui.theme.FontColor

@Composable
fun AnnonceTexte(navController: NavController) {
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    var textContenu by remember { mutableStateOf(TextFieldValue()) }



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Synthétiseur vocal",
            fontSize = 25.sp,
            color = FontColor
        )
       
        Spacer(modifier = Modifier.height(40.dp))
       

        TextField(
            value = textValue,
            onValueChange = {
                textValue = it
            },
            label = { Text("Entrez le nom") },
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(20.dp))


        TextField(
            value = textContenu,
            onValueChange = {
                textContenu = it
            },
            label = { Text("Entrez le contenu qui sera lu par le synthétiseur vocal") },
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // FAIRE CHOIX DE LANGUE POUR SYNTHETISEUR
        Text(
            text = "Langue de la voix :",
            color = FontColor
        )


        Spacer(modifier = Modifier.height(50.dp))

        
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Enregistrer",
                color = Color.White
            )
        }

    }
}