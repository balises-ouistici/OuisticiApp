package com.example.ouistici.ui.parametresAppli

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.model.Langue
import com.example.ouistici.model.LangueManager
import com.example.ouistici.ui.theme.FontColor


@Composable
fun ParametresAppli(navController: NavController) {
    var selectedLangue by remember { mutableStateOf(LangueManager.langueActuelle) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = stringResource(R.string.parametres_de_l_application),
            fontSize = 25.sp,
            color = FontColor
        )


        ExposedDropdownMenu(
            items = LangueManager.languesDisponibles,
            selectedItem = selectedLangue,
            onItemSelected = { langue ->
                selectedLangue = langue
                LangueManager.langueActuelle = langue
                // Possibilité de faire la traduction ici
                // en utilisant par exemple une fonction de traduction appropriée
                // en fonction de la langue sélectionnée
                // updateAppLanguage(langue.code, context)
            }
        )
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = stringResource(R.string.enregistrer),
                color = Color.White
            )
        }
    }
}

@Composable
fun ExposedDropdownMenu(
    items: List<Langue>,
    selectedItem: Langue,
    onItemSelected: (Langue) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row (verticalAlignment = Alignment.CenterVertically) {


        Text(
            text = stringResource(R.string.changer_la_langue_de_l_application),
            color = FontColor
        )
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clickable(onClick = { expanded = true })
        ) {

            Text(
                text = selectedItem.getLangueName(),
                fontSize = 16.sp,
                color = FontColor
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { langue ->
                    DropdownMenuItemLangue(
                        langue = langue,
                        onClick = {
                            onItemSelected(it)
                            expanded = false
                        }
                    )
                }
            }
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

