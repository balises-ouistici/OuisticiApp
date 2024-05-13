package com.example.ouistici.ui.annonceTexte

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.Langue
import com.example.ouistici.model.LangueManager
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.ui.parametresAppli.DropdownMenuItemLangue
import com.example.ouistici.ui.theme.FontColor

@Composable
fun AnnonceTexte(navController: NavController, balise: Balise) {
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    var textContenu by remember { mutableStateOf(TextFieldValue()) }
    var textValueInput by remember { mutableStateOf("") }
    var textContenuInput by remember { mutableStateOf("") }
    var langueSelectionnee by remember { mutableStateOf(Langue("", "")) }
    var expanded by remember { mutableStateOf(false) }

    var context = LocalContext.current



    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
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
                textValueInput = it.text
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
                textContenuInput = it.text
            },
            label = { Text("Entrez le contenu qui sera lu par le synthétiseur vocal") },
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // FAIRE CHOIX DE LANGUE POUR SYNTHETISEUR
        Row (verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Langue de la voix :",
                color = FontColor
            )


            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = { expanded = true })
            ) {

                if ( langueSelectionnee.code == "" && langueSelectionnee.nom == "" ) {
                    Text(
                        text = "Choisir",
                        fontSize = 16.sp,
                        color = FontColor
                    )
                } else {
                    Text(
                        text = langueSelectionnee.getLangueName(),
                        fontSize = 16.sp,
                        color = FontColor
                    )
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    LangueManager.languesDisponibles.forEach { langue ->
                        DropdownMenuItemLangue(
                            langue = langue,
                            onClick = {
                                langueSelectionnee = langue
                                expanded = false
                            }
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(50.dp))

        
        Button(
            onClick = {
                if ( textValueInput != "" && textContenuInput != "" && langueSelectionnee.code != "" && langueSelectionnee.nom != "" ) {
                    balise.annonces.add(Annonce(balise.createId() ,textValueInput, TypeAnnonce.TEXTE, null, textContenuInput, langueSelectionnee))
                    Toast.makeText(
                        context,
                        "Annonce ajoutée",
                        Toast.LENGTH_LONG)
                        .show()
                    navController.navigate("annonceTexte")
                } else {
                    Toast.makeText(
                        context,
                        "Action impossible, vous devez remplir les champs",
                        Toast.LENGTH_LONG)
                        .show()

                }
            },
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


@Composable
fun DropdownMenuItemLangue(
    langue: Langue,
    onClick: (Langue) -> Unit
) {
    Text(
        text = langue.getLangueName(),
        fontSize = 16.sp,
        modifier = Modifier
            .clickable(onClick = { onClick(langue) })
            .padding(vertical = 8.dp, horizontal = 16.dp)
    )
}