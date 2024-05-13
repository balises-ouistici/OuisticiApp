package com.example.ouistici.ui.choixAnnonce

import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TableHeaderColor
import java.time.LocalTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChoixAnnonce(navController: NavController, balise: Balise) {
    val showAddPlageHorairePopup = remember { mutableStateOf(false) }
    val showDefaultMessagePopup = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            // .verticalScroll(rememberScrollState())
        ,
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

        Row(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "Système de plage horaires : ",
                color = Color.Black
            )

            OnOffButton(balise, navController)
        }

        Spacer(modifier = Modifier.height(30.dp))

        if ( balise.sysOnOff ) {
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
                        balise.plages.add(plageHoraire)
                        showAddPlageHorairePopup.value = false
                    },
                    onDismiss = { showAddPlageHorairePopup.value = false },
                    navController = navController
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (balise.sysOnOff) 0f else 0.5f))
            ) {
                TableScreen(balise, navController)
            }
        }
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
            modifier = Modifier
                .width(300.dp)
                .verticalScroll(rememberScrollState())
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
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text(text = "Annuler", color = Color.White)
                        }
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
    var heureDebut by remember { mutableStateOf<LocalTime?>(null) }
    var heureFin by remember { mutableStateOf<LocalTime?>(null) }


    val context = LocalContext.current


    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]


    val mTimeStart = remember { mutableStateOf("") }

    val mTimeEnd = remember { mutableStateOf("") }



    val mTimePickerDialogHeureDebut = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            heureDebut = LocalTime.of(mHour,mMinute)
            mTimeStart.value = "$mHour:$mMinute"
        }, mHour, mMinute, true
    )

    val mTimePickerDialogHeureFin = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            heureFin = LocalTime.of(mHour,mMinute)
            mTimeEnd.value = "$mHour:$mMinute"
        }, mHour, mMinute, true
    )



    // Contenu du popup
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(300.dp)
                .verticalScroll(rememberScrollState())
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


                if ( heureDebut == null ) {
                    Text(text = "Heure début : Aucune")
                } else {
                    Text(text = "Heure début : $heureDebut")
                }
                Button(
                    onClick = { mTimePickerDialogHeureDebut.show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
                ) {
                    Text(text = "Choisir heure début", color = Color.White)
                }


                Spacer(modifier = Modifier.height(16.dp))


                if ( heureFin == null ) {
                    Text(text = "Heure fin : Aucune")
                } else {
                    Text(text = "Heure fin : $heureFin")
                }

                Button(
                    onClick = { mTimePickerDialogHeureFin.show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
                ) {
                    Text(text = "Choisir heure fin", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text = "Annuler", color = Color.White)
                    }
                    Button(
                        onClick = {
                            // Création de la plage horaire
                            if (selectedAnnonce != null && selectedJours.isNotEmpty() && heureDebut != null && heureFin != null) {
                                if ( heureDebut!! >= heureFin!! ) {
                                    Toast.makeText(
                                        context,
                                        "L'heure de fin ne peut pas être avant celle du début !",
                                        Toast.LENGTH_LONG)
                                        .show()
                                } else {
                                    balise.plages.add(PlageHoraire(selectedAnnonce!!, selectedJours, heureDebut!!, heureFin!!))
                                    Toast.makeText(
                                        context,
                                        "Plage horaire ajoutée",
                                        Toast.LENGTH_LONG)
                                        .show()
                                    onDismiss()
                                    navController.navigate("manageAnnonce")
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Informations manquantes",
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = "Ajouter", color = Color.White)
                    }
                }
            }
        }
    }
}







@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModifyPlageHorairePopup(
    balise : Balise,
    plageHoraire : PlageHoraire? = null,
    navController: NavController,
    onDismiss: () -> Unit
) {
    // États pour les champs du formulaire
    var selectedAnnonce by remember { mutableStateOf(plageHoraire?.nomMessage) }
    var selectedJours by remember { mutableStateOf(plageHoraire?.jours ?: emptyList()) }
    var heureDebut by remember { mutableStateOf(plageHoraire?.heureDebut) }
    var heureFin by remember { mutableStateOf(plageHoraire?.heureFin) }


    val context = LocalContext.current




    val mTimeStart = remember { mutableStateOf("") }

    val mTimeEnd = remember { mutableStateOf("") }


    val mTimePickerDialogHeureDebut = heureDebut?.let {
        TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            heureDebut = LocalTime.of(mHour,mMinute)
            mTimeStart.value = "$mHour:$mMinute"
        }, it.hour, it.minute, true
    )
    }

    val mTimePickerDialogHeureFin = heureFin?.let {
        TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            heureFin = LocalTime.of(mHour,mMinute)
            mTimeEnd.value = "$mHour:$mMinute"
        }, it.hour, it.minute, true
    )
    }



    // Contenu du popup
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Modifier une plage horaire", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Choisir annonce :",
                    fontWeight = FontWeight.SemiBold
                )

                AnnonceList(
                    annonces = balise.annonces,
                    selectedAnnonce = selectedAnnonce,
                    onAnnonceSelected = { selectedAnnonce = it }
                )
                Spacer(modifier = Modifier.height(16.dp))


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


                if ( heureDebut == null ) {
                    Text(text = "Heure début : Aucune")
                } else {
                    Text(text = "Heure début : $heureDebut")
                }
                Button(
                    onClick = {
                        mTimePickerDialogHeureDebut?.show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
                ) {
                    Text(text = "Choisir heure début", color = Color.White)
                }


                Spacer(modifier = Modifier.height(16.dp))


                if ( heureFin == null ) {
                    Text(text = "Heure fin : Aucune")
                } else {
                    Text(text = "Heure fin : $heureFin")
                }

                Button(
                    onClick = {
                        mTimePickerDialogHeureFin?.show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF0F9D58))
                ) {
                    Text(text = "Choisir heure fin", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text = "Annuler", color = Color.White)
                    }
                    Button(
                        onClick = {
                            // Création de la plage horaire
                            if (selectedAnnonce != null && selectedJours.isNotEmpty() && heureDebut != null && heureFin != null) {
                                if ( heureDebut!! >= heureFin!! ) {
                                    Toast.makeText(
                                        context,
                                        "L'heure de fin ne peut pas être avant celle du début !",
                                        Toast.LENGTH_LONG)
                                        .show()
                                } else {
                                    plageHoraire?.nomMessage = selectedAnnonce as Annonce
                                    plageHoraire?.jours = selectedJours
                                    plageHoraire?.heureDebut = heureDebut as LocalTime
                                    plageHoraire?.heureFin = heureFin as LocalTime

                                    Toast.makeText(
                                        context,
                                        "Plage horaire modifiée",
                                        Toast.LENGTH_LONG)
                                        .show()
                                    onDismiss()
                                    navController.navigate("manageAnnonce")
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Informations manquantes",
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = "Modifier", color = Color.White)
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
    val joursSemaine = JoursSemaine.entries.toTypedArray()

    Box(
        modifier = Modifier
            .height(200.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = jour.name)
                }
            }
        }
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
            .height(30.dp),
        color = textColor
    )
}


@Composable
fun RowScope.TableJoursCell(
    jours: List<JoursSemaine>,
    weight: Float,
    textColor: Color,
) {
    if ( jours.count() == 7 ) {
        Text(
            text = "Tous les jours",
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .height(50.dp),
            color = textColor
        )
    } else {
        Text(
            text = jours.joinToString(", ") { it.name.take(2) },
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
                .height(50.dp),
            color = textColor
        )
    }
}

@Composable
fun RowScope.TableCells(
    text: String,
    weight: Float,
    textColor: Color,
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
                .align(Alignment.Center),
            color = textColor
        )
    }
}



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


    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(TableHeaderColor)) {
                TableHeaderCell(text = "Nom mess", weight = column1Weight, textColor = Color.Black)
                TableHeaderCell(text = "Jours", weight = columnJours, textColor = Color.Black)
                TableHeaderCell(text = "Horaires", weight = column2Weight, textColor = Color.Black)
                TableHeaderCell(text = "", weight = .26f, textColor = Color.Black)
            }
        }

        // SYSTEME D'AJOUT DES PLAGES HORAIRES QUAND BOUTON APPUYE
        items((balise.plages as List<PlageHoraire>)) { plage ->
            Row(Modifier.fillMaxWidth()) {
                TableCells(
                    text = plage.nomMessage.nom,
                    weight = column1Weight,
                    textColor = Color.Black
                )

                TableJoursCell(
                    jours = plage.jours,
                    weight = columnJours,
                    textColor = Color.Black
                )

                TableCells(
                    text = "de " + plage.heureDebut.toString() + "\nà " + plage.heureFin.toString(),
                    weight = column2Weight,
                    textColor = Color.Black
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
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier plage horaire",
                        modifier = Modifier.size(10.dp)
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
                        balise.plages.remove(plage)
                        Toast.makeText(
                            context,
                            "Plage horaire supprimée",
                            Toast.LENGTH_LONG)
                            .show()
                        navController.navigate("manageAnnonce")
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
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Supprimer plage horaire",
                        modifier = Modifier.size(10.dp)
                    )
                }

            }
        }

    }
}


@Composable
fun OnOffButton(balise: Balise, navController: NavController) {
    val checkedState = remember { mutableStateOf(balise.sysOnOff) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checkedState.value,
            onCheckedChange = { isChecked ->
                checkedState.value = isChecked
                balise.sysOnOff = checkedState.value
                navController.navigate("manageAnnonce")
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


