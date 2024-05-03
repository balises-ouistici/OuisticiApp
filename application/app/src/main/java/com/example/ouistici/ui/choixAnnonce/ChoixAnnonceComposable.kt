package com.example.ouistici.ui.choixAnnonce

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.JoursSemaine
import com.example.ouistici.model.PlageHoraire
import com.example.ouistici.ui.infosBalise.TableCell
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TableHeaderColor
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChoixAnnonce(navController: NavController, balise: Balise) {
    val showAddPlageHorairePopup = remember { mutableStateOf(false) }
    val showDefaultMessagePopup = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Choix des annonces",
            fontSize = 25.sp,
            color = FontColor
        )


        // Boîte son par défaut
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .width(350.dp),
            shape = RoundedCornerShape(10.dp),
            color = BodyBackground,
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .padding(15.dp)
            ) {
                Text(
                    text = "Choisir annonce par défaut : ",
                    color = FontColor,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { showDefaultMessagePopup.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                       Text(
                           text = "Choisir",
                           color = Color.White
                       )
                    }

                    if ( balise.defaultMessage == null ) {
                        Text(
                            text = "Aucun",
                            color = FontColor
                        )
                    } else {
                        Text(
                            text = balise.defaultMessage!!.nom, // Afficher le message par défaut de la balise
                            color = FontColor
                        )
                    }



                    if (showDefaultMessagePopup.value) {
                        DefaultMessagePopup(
                            balise = balise,
                            navController = navController,
                        ) { showDefaultMessagePopup.value = false }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row {
            Text(
                text = "Système de plage horaires : ",
                color = Color.Black
            )

            OnOffButton()
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { showAddPlageHorairePopup.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Ajouter une plage horaire",
                color = Color.White
            )
        }

        if (showAddPlageHorairePopup.value) {
            AddPlageHorairePopup(
                balise = balise,
                onPlageHoraireAdded = { plageHoraire ->
                    balise.plage?.add(plageHoraire)
                    showAddPlageHorairePopup.value = false
                },
                onDismiss = { showAddPlageHorairePopup.value = false },
                navController = navController
            )
        }

        TableScreen(balise)
    }
}


@Composable
fun DefaultMessagePopup(
    balise: Balise,
    navController: NavController,
    onDismiss: () -> Unit
) {
    var selectedAnnonce: Annonce? by remember { mutableStateOf(null) }

    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Liste des annonces", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                AnnonceDefaultMessageList(
                    annonces = balise.annonces,
                    selectedAnnonce = selectedAnnonce,
                    onAnnonceSelected = { selectedAnnonce = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // Création de la plage horaire
                            if (selectedAnnonce != null) {
                                balise.defaultMessage = selectedAnnonce
                                Toast.makeText(
                                    context,
                                    "Nouvelle annonce par défaut",
                                    Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Action impossible",
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = "Ajouter", color = Color.White)
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text = "Annuler", color = Color.White)
                    }
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Liste des annonces", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                AnnonceDefaultMessageList(
                    annonces = balise.annonces,
                    selectedAnnonce = selectedAnnonce,
                    onAnnonceSelected = { selectedAnnonce = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if ( balise.annonces.isEmpty() ) {
                        Button(
                            onClick = {
                                navController.navigate("addAnnonce")
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(text = "Créer annonce", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (selectedAnnonce != null) {
                                    balise.defaultMessage = selectedAnnonce
                                    Toast.makeText(
                                        context,
                                        "Nouvelle annonce par défaut",
                                        Toast.LENGTH_LONG)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Action impossible",
                                        Toast.LENGTH_LONG)
                                        .show()
                                }
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(text = "Définir", color = Color.White)
                        }
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text = "Annuler", color = Color.White)
                    }
                }
            }
        }

    }
}






@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddPlageHorairePopup(
    balise : Balise,
    onPlageHoraireAdded: (PlageHoraire) -> Unit,
    onDismiss: () -> Unit,
    navController: NavController
) {
    // États pour les champs du formulaire
    var selectedAnnonce by remember { mutableStateOf<Annonce?>(null) }
    var selectedJours by remember { mutableStateOf<List<JoursSemaine>>(emptyList()) }
    var heureDebut by remember { mutableStateOf(LocalTime.of(0,0)) }
    var heureFin by remember { mutableStateOf(LocalTime.of(0,0)) }

    val context = LocalContext.current

    // Contenu du popup
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Ajouter une plage horaire", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Choisir annonce :",
                    fontWeight = FontWeight.SemiBold
                )
                if ( balise.annonces.isEmpty() )  {
                    Text(
                        text = "Créez d'abord une annonce"
                    )
                    Spacer(modifier = Modifier.height(65.dp))
                } else {
                    AnnonceList(
                        annonces = balise.annonces,
                        selectedAnnonce = selectedAnnonce,
                        onAnnonceSelected = { selectedAnnonce = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "Choisir les jours d'activations :",
                    fontWeight = FontWeight.SemiBold
                )
                JoursSemaineSelector(
                    selectedJours = selectedJours,
                    onJoursSelected = { selectedJours = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Choisir les périodes :",
                    fontWeight = FontWeight.SemiBold
                )
                TimePicker(
                    heure = heureDebut,
                    onHeureSelected = { heureDebut = it },
                    label = "Heure de début"
                )
                Spacer(modifier = Modifier.height(16.dp))
                TimePicker(
                    heure = heureFin,
                    onHeureSelected = { heureFin = it },
                    label = "Heure de fin"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // Création de la plage horaire
                            if (selectedAnnonce != null && selectedJours.isNotEmpty()) {
                                balise.plage?.add(PlageHoraire(selectedAnnonce!!, selectedJours, heureDebut, heureFin))
                                Toast.makeText(
                                    context,
                                    "Annonce ajoutée",
                                    Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Informations manquantes",
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
                            onDismiss()
                            navController.navigate("manageAnnonce")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = "Ajouter", color = Color.White)
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text = "Annuler", color = Color.White)
                    }
                }
            }
        }
    }
}


@Composable
fun AnnonceList(
    annonces: List<Annonce>,
    selectedAnnonce: Annonce?,
    onAnnonceSelected: (Annonce) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .height(100.dp)
    ) {
        items(annonces) { annonce ->
            val isSelected = annonce == selectedAnnonce
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAnnonceSelected(annonce) }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = annonce.nom)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Green // Change the color as desired
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun AnnonceDefaultMessageList(
    annonces: List<Annonce>,
    selectedAnnonce: Annonce?,
    onAnnonceSelected: (Annonce) -> Unit
) {
    if ( annonces.isEmpty() ) {
        Text(
            text = "Créez d'abord une annonce"
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .height(200.dp)
        ) {
            items(annonces) { annonce ->
                val isSelected = annonce == selectedAnnonce
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAnnonceSelected(annonce) }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = annonce.nom)
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Green // Change the color as desired
                            )
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun JoursSemaineSelector(
    selectedJours: List<JoursSemaine>,
    onJoursSelected: (List<JoursSemaine>) -> Unit
) {
    val joursSemaine = JoursSemaine.values()

    Box(
        modifier = Modifier
            .height(200.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            for (jour in joursSemaine) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedJours.contains(jour),
                        onCheckedChange = { isChecked ->
                            val updatedList = if (isChecked) {
                                selectedJours + jour
                            } else {
                                selectedJours - jour
                            }
                            onJoursSelected(updatedList)
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(text = jour.name)
                }
            }
        }
    }
}





@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePicker(
    heure: LocalTime,
    onHeureSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    var selectedTime by remember { mutableStateOf(heure) }

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(text = label)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Afficher l'heure sélectionnée
            Text(text = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")))
            Spacer(modifier = Modifier.width(8.dp))
            // Ouvrir le sélecteur d'heure au clic
            TimePickerIconButton(selectedTime, onHeureSelected)
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerIconButton(
    selectedTime: LocalTime,
    onHeureSelected: (LocalTime) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        Icon(Icons.Filled.DateRange, contentDescription = "Select time")
    }

    if (showDialog) {
        ShowTimePickerDialog(selectedTime = selectedTime, onHeureSelected = onHeureSelected) {
            showDialog = false
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowTimePickerDialog(
    selectedTime: LocalTime,
    onHeureSelected: (LocalTime) -> Unit,
    onDismissRequest: () -> Unit
) {
    val timeState = remember { mutableStateOf(selectedTime) }

    // Afficher un dialogue personnalisé pour sélectionner l'heure
    Dialog(onDismissRequest = onDismissRequest) {
        TimePicker(
            heure = timeState.value,
            onHeureSelected = { heure ->
                timeState.value = heure
                onHeureSelected(heure)
                onDismissRequest()
            },
            modifier = Modifier.padding(16.dp)
        )
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
fun RowScope.TableJoursCell(
    jours: List<JoursSemaine>,
    weight: Float,
    textColor: Color,
) {
    Text(
        text = jours.joinToString(", ") { it.name },
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



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TableScreen(balise : Balise) {
    val column1Weight = .3f
    val column2Weight = .2f

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(TableHeaderColor)) {
                TableHeaderCell(text = "Nom mess", weight = column1Weight, textColor = Color.Black)
                TableHeaderCell(text = "Jours", weight = column1Weight, textColor = Color.Black)
                TableHeaderCell(text = "H Dep", weight = column2Weight, textColor = Color.Black)
                TableHeaderCell(text = "H Fin", weight = column2Weight, textColor = Color.Black)
            }
        }

        // SYSTEME D'AJOUT DES PLAGES HORAIRES QUAND BOUTON APPUYE
        items((balise.plage as List<PlageHoraire>)) { plages ->
            Row(Modifier.fillMaxWidth()) {
                TableCell(
                    text = plages.nomMessage.nom,
                    weight = column1Weight,
                    textColor = Color.Black
                )

                TableJoursCell(
                    jours = plages.jours,
                    weight = column1Weight,
                    textColor = Color.Black
                )

                TableCell(
                    text = plages.heureDebut.toString(),
                    weight = column1Weight,
                    textColor = Color.Black
                )

                TableCell(
                    text = plages.heureFin.toString(),
                    weight = column1Weight,
                    textColor = Color.Black
                )

            }
        }

    }
}


@Composable
fun OnOffButton() {
    val checkedState = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checkedState.value,
            onCheckedChange = { isChecked ->
                checkedState.value = isChecked
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
            color = Color.Black
        )
    }
}


