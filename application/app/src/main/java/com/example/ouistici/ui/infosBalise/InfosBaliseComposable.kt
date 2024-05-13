package com.example.ouistici.ui.infosBalise

import android.app.ProgressDialog.show
import android.app.TimePickerDialog
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.JoursSemaine
import com.example.ouistici.model.PlageHoraire
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.ui.baliseViewModel.BaliseViewModel
import com.example.ouistici.ui.choixAnnonce.AnnonceList
import com.example.ouistici.ui.choixAnnonce.DefaultMessagePopup
import com.example.ouistici.ui.choixAnnonce.JoursSemaineSelector
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TableHeaderColor
import com.example.ouistici.ui.theme.TestButtonColor
import java.io.File
import java.time.LocalTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InfosBalise(
    navController: NavController,
    balise: Balise,
    player: AndroidAudioPlayer
) {

    var currentStep by remember { mutableStateOf(1) }


    val showModifyInfosBalisePopup = remember { mutableStateOf(false) }


    var sliderPosition by remember {
        mutableFloatStateOf(0f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Informations balise",
            fontSize = 25.sp,
            color = FontColor
        )

        // Boite avec nom, lieu, message par défaut
        Row {
            Surface(
                modifier = Modifier
                    .padding(8.dp)
                    .widthIn(max = 280.dp)
                    .height(70.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(2.dp, color = Color.Black)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Nom balise : " + balise.nom,
                        color = FontColor
                    )

                    if ( balise.lieu == null ) {
                        Text(
                            text = "Lieu : Non défini",
                            color = FontColor
                        )
                    } else {
                        Text(
                            text = "Lieu : " + balise.lieu,
                            color = FontColor
                        )
                    }

                    if ( balise.defaultMessage == null ) {
                        Text(
                            text = "Message défaut : Aucun",
                            color = FontColor
                        )
                    } else {
                        Text(
                            text = "Message défaut : " + balise.defaultMessage?.nom,
                            color = FontColor
                        )
                    }
                }
            }
            // Bouton pour modifier le nom de la balise
            OutlinedButton(
                onClick = {
                    showModifyInfosBalisePopup.value = true
                },
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = BodyBackground
                ),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .width(50.dp)
                    .height(75.dp)
                    .padding(vertical = 15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Changer les informations de la balise",
                    modifier = Modifier.size(20.dp)
                )
            }

            if ( showModifyInfosBalisePopup.value ) {
                ModifyInfosBalisePopup(
                    balise = balise,
                ) { showModifyInfosBalisePopup.value = false }
            }
        }



        Text(
            text = "Liste des annonces",
            fontSize = 25.sp,
            color = FontColor
        )
        // Liste de toutes les annonces
        TableScreen(balise = balise, player = player, navController = navController)




        // Boite avec Volume de la balise
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .widthIn(max = 300.dp),
            shape = RoundedCornerShape(10.dp),
            color = BodyBackground,
            border = BorderStroke(2.dp, Color.Black),

            ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Volume de la balise",
                    fontSize = 25.sp,
                    color = FontColor
                )
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Black,
                        activeTrackColor = Color.Black,
                        inactiveTrackColor = Color.White
                    ),
                    valueRange = 0f..100f
                )

                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(TestButtonColor)
                ) {
                    Text(
                        text = "Tester sur la balise",
                        color = Color.White
                    )
                }
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
    }
}

// POPUP POUR INFOS BALISES

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModifyInfosBalisePopup(
    balise : Balise,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var nomBalise by remember { mutableStateOf(balise.nom) }
    var lieuBalise by remember { mutableStateOf(balise.lieu ?: "") }

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
                Text(text = "Informations balises", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nom balise :",
                    fontWeight = FontWeight.SemiBold
                )
                TextField(
                    value = nomBalise,
                    onValueChange = { nomBalise = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Lieu :",
                    fontWeight = FontWeight.SemiBold
                )
                TextField(
                    value = lieuBalise,
                    onValueChange = { lieuBalise = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pour changer le message par défaut, allez dans modifier et cliquez sur le bouton \"Choisir\".",
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if ( nomBalise != "" ) {
                                if ( lieuBalise != "" ) {
                                    balise.lieu = lieuBalise
                                } else {
                                    balise.lieu = null
                                }
                                balise.nom = nomBalise
                                Toast.makeText(
                                    context,
                                    "Informations modifiées",
                                    Toast.LENGTH_LONG)
                                    .show()
                                onDismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "La balise doit avoir un nom !",
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
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



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModifyAnnoncesBaliseTextePopup(
    annonce: Annonce,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var nomAnnonce by remember { mutableStateOf(annonce.nom) }
    var contenuAnnonceTexte by remember { mutableStateOf(annonce.contenu ?: "") }

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
                Text(
                    text = "Informations annonce",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nom annonce :",
                    fontWeight = FontWeight.SemiBold
                )
                TextField(
                    value = nomAnnonce,
                    onValueChange = { nomAnnonce = it },
                    modifier = Modifier.fillMaxWidth()
                )



                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Contenu :",
                    fontWeight = FontWeight.SemiBold
                )
                TextField(
                    value = contenuAnnonceTexte,
                    onValueChange = { contenuAnnonceTexte = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Langue :",
                    fontWeight = FontWeight.SemiBold
                )

                // FAIRE CHANGEMENT DE LANGUE


                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (nomAnnonce != "") {

                                annonce.contenu = contenuAnnonceTexte
                                // PAREIL SAUVEGARDER LA LANGUE DE L'ANNONCE

                                annonce.nom = nomAnnonce
                                Toast.makeText(
                                    context,
                                    "Informations modifiées",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                navController.navigate("infosBalise")
                                onDismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "L'annonce doit avoir un nom !",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
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




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModifyAnnoncesBaliseAudioPopup(
    annonce: Annonce,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var nomAnnonce by remember { mutableStateOf(annonce.nom) }

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
                Text(
                    text = "Informations annonce",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nom annonce :",
                    fontWeight = FontWeight.SemiBold
                )
                TextField(
                    value = nomAnnonce,
                    onValueChange = { nomAnnonce = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (nomAnnonce != "") {
                                annonce.nom = nomAnnonce
                                Toast.makeText(
                                    context,
                                    "Information modifiée",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                navController.navigate("infosBalise")
                                onDismiss()
                            } else {
                                Toast.makeText(
                                    context,
                                    "L'annonce doit avoir un nom !",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
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





// TABLEAU

@Composable
fun RowScope.TableCell(
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
fun RowScope.TableAudioCell(
    audioFile: File?,
    weight: Float,
    player: AndroidAudioPlayer
) {
    val context = LocalContext.current
    Row(
        Modifier
            .weight(weight)
            .border(1.dp, Color.Black)
            .height(46.dp)
            .padding(8.dp)
    ) {
        var mediaPlayer : MediaPlayer? = null

        Button(
            onClick = {
                player.playFile(audioFile ?: return@Button)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play arrow"
            )
        }
        Button(
            onClick = {
                player.stop()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = "||"
            )
        }

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TableScreen(balise : Balise, player: AndroidAudioPlayer, navController: NavController) {
    val column1Weight = .3f
    val column2Weight = .44f
    val column3Weight = .13f

    val context = LocalContext.current

    val showModifyAnnonceTextPopup = remember { mutableStateOf(false) }
    val showModifyAnnonceAudioPopup = remember { mutableStateOf(false) }

    val idAnnonceEdit = remember { mutableIntStateOf(0) }


    if (balise.annonces.isEmpty()) {
        Column(
            modifier = Modifier
                .height(190.dp)
        ) {
            Text(
                text = "Il n'y a pas d'annonces",
                color = FontColor
            )
        }
        Spacer(modifier = Modifier.padding(12.dp))
    } else {
        LazyColumn(
            Modifier
                .padding(16.dp)
                .height(185.dp)
        ) {
            item {
                Row(Modifier.background(TableHeaderColor)) {
                    TableCell(text = "Nom", weight = column1Weight, textColor = Color.Black)
                    TableCell(text = "Contenu", weight = column2Weight, textColor = Color.Black)
                    TableCell(text= "", weight = .26f, textColor = Color.Black)
                }
            }

            items((balise.annonces as List<Annonce>)) { annonce ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(
                        text = annonce.nom,
                        weight = column1Weight,
                        textColor = Color.Black
                    )

                    if (annonce.type == TypeAnnonce.TEXTE) {
                        annonce.contenu?.let {
                            TableCell(
                                text = it,
                                weight = column2Weight,
                                textColor = Color.Black
                            )

                            OutlinedButton(
                                onClick = {
                                    idAnnonceEdit.intValue = balise.annonces.indexOf(annonce)
                                    showModifyAnnonceTextPopup.value = true
                                },
                                shape = RectangleShape,
                                border = BorderStroke(1.dp, Color.Black),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.Black,
                                    containerColor = BodyBackground
                                ),
                                contentPadding = PaddingValues(8.dp),
                                modifier = Modifier
                                    .height(45.dp)
                                    .weight(column3Weight)
                                    .border(1.dp, Color.Black)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Changer le nom de l'annonce",
                                    modifier = Modifier.size(15.dp)
                                )
                            }


                            if (showModifyAnnonceTextPopup.value) {
                                ModifyAnnoncesBaliseTextePopup(
                                    annonce = balise.annonces.get(idAnnonceEdit.intValue),
                                    navController = navController
                                ) { showModifyAnnonceTextPopup.value = false }
                            }

                        }
                    }

                    if (annonce.type == TypeAnnonce.AUDIO) {
                        TableAudioCell(
                            audioFile = annonce.audio,
                            weight = column2Weight,
                            player = player
                        )

                        OutlinedButton(
                            onClick = {
                                idAnnonceEdit.intValue=balise.annonces.indexOf(annonce)
                                showModifyAnnonceAudioPopup.value = true
                            },
                            shape = RectangleShape,
                            border = BorderStroke(1.dp, Color.Black),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Black,
                                containerColor = BodyBackground
                            ),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier
                                .height(45.dp)
                                .weight(column3Weight)
                                .border(1.dp, Color.Black)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Changer le nom de l'annonce",
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        if (showModifyAnnonceAudioPopup.value) {
                            ModifyAnnoncesBaliseAudioPopup(
                                annonce = balise.annonces.get(idAnnonceEdit.intValue),                                navController = navController
                            ) { showModifyAnnonceAudioPopup.value = false }
                        }

                    }

                    OutlinedButton(
                        onClick = {
                            var verif = false
                            for (i in balise.plages ) {
                                if ( annonce == i.nomMessage ) {
                                    verif = true
                                    Toast.makeText(
                                        context,
                                        "Annonce non supprimable, elle est utilisée dans une plage horaire",
                                        Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            if ( verif == false ) {
                                if ( balise.defaultMessage == annonce ) {
                                    balise.defaultMessage = null
                                }
                                balise.annonces.remove(annonce)
                                Toast.makeText(
                                    context,
                                    "Annonce supprimée",
                                    Toast.LENGTH_LONG)
                                    .show()
                                navController.navigate("infosBalise")
                            }
                        },
                        shape = RectangleShape,
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = BodyBackground
                        ),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .height(45.dp)
                            .weight(column3Weight)
                            .border(1.dp, Color.Black)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Supprimer annonce",
                            modifier = Modifier.size(15.dp)
                        )
                    }

                }
            }
        }
    }
}