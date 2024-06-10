package com.example.ouistici.ui.infosBalise

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.ToastUtil
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TableHeaderColor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @brief Composable function for rendering a table cell within a row.
 *
 * @param text The text content to be displayed in the cell.
 * @param weight The weight of the cell within the row.
 * @param textColor The color of the text content.
 */
@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    textColor: Color,
    textSemantics: String
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(30.dp)
            .semantics { contentDescription = "$textSemantics $text" },
        color = textColor,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TableCellHeader(
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
            .height(30.dp)
            .semantics { contentDescription = "Titre de colonne du tableau, ${text}." },
        color = textColor,
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TableCellButtons(
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
            .height(30.dp)
            .semantics {
                this.invisibleToUser()
            },
        color = textColor
    )
}


/**
 * @brief Composable function for rendering a table cell with audio controls within a row.
 *
 * @param audioFile The audio file to be played.
 * @param weight The weight of the cell within the row.
 * @param player The Android audio player used to play the audio file.
 */
@Composable
fun RowScope.TableAudioCell(
    audioFile: File?,
    weight: Float,
    player: AndroidAudioPlayer,
    annonce: Annonce
) {
    val context = LocalContext.current

    Row(
        Modifier
            .weight(weight)
            .border(1.dp, Color.Black)
            .height(46.dp)
            .padding(8.dp)
    ) {
        Button(
            onClick = {
                if ( audioFile == null ) {
                    val apiService = RestApiService()
                    apiService.downloadAudio(annonce.id) { bytes ->
                        if (bytes != null) {
                            val localFile = saveAudioFileLocally(context, bytes, annonce.filename)
                            if (localFile != null) {
                                player.playFile(localFile)
                            }
                        } else {
                            ToastUtil.showToast(context, "Échec lors de la récupération de l'audio")
                        }
                    }
                } else {
                    player.playFile(audioFile)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.CenterVertically)
                .semantics { contentDescription = "Écouter l'audio de l'annonce" }
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = ""
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
                .semantics {
                    contentDescription = "Arrêter la lecture audio"
                }
        ) {
            Text(
                text = "||"
            )
        }

    }
}


/**
 * @brief Composable function for rendering a table screen with announcements for a beacon.
 *
 * @param balise The beacon containing announcements.
 * @param player The Android audio player used for playing audio announcements.
 * @param navController The navigation controller for managing navigation within the app.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TableScreen(balise : Balise, player: AndroidAudioPlayer, navController: NavController) {
    val column1Weight = .3f
    val column2Weight = .44f
    val column3Weight = .13f

    val context = LocalContext.current

    val showModifyAnnonceTextPopup = remember { mutableStateOf(false) }
    val showModifyAnnonceAudioPopup = remember { mutableStateOf(false) }
    val showConfirmDeletePopup = remember { mutableStateOf(false) }


    val idAnnonceEdit = remember { mutableIntStateOf(0) }



    if (balise.annonces.isEmpty()) {
        Column(
            modifier = Modifier
                .height(190.dp)
        ) {
            Text(
                text = stringResource(R.string.il_n_y_a_pas_d_annonces),
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
                    TableCellHeader(text = stringResource(R.string.nom), weight = column1Weight, textColor = Color.Black)
                    TableCellHeader(text = stringResource(R.string.contenu_tableau), weight = column2Weight, textColor = Color.Black)
                    TableCellButtons(text= "", weight = .26f, textColor = Color.Black)
                }
            }

            items((balise.annonces as List<Annonce>)) { annonce ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(
                        text = annonce.nom,
                        weight = column1Weight,
                        textColor = Color.Black,
                        textSemantics = "Nom de l'annonce"
                    )

                    if (annonce.type == TypeAnnonce.TEXTE) {
                        annonce.contenu?.let {
                            TableCell(
                                text = it,
                                weight = column2Weight,
                                textColor = Color.Black,
                                textSemantics = "Contenu de l'annonce"
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
                                    .semantics {
                                        contentDescription =
                                            "Modifier le nom et le contenu de l'annonce ${annonce.nom}"
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "",
                                    modifier = Modifier.size(15.dp)
                                )
                            }


                            if (showModifyAnnonceTextPopup.value) {
                                ModifyAnnoncesBaliseTextePopup(
                                    annonce = balise.annonces[idAnnonceEdit.intValue],
                                    navController = navController,
                                    balise = balise
                                ) { showModifyAnnonceTextPopup.value = false }
                            }

                        }
                    }

                    if (annonce.type == TypeAnnonce.AUDIO) {
                        TableAudioCell(
                            audioFile = annonce.audio,
                            weight = column2Weight,
                            player = player,
                            annonce = annonce
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
                                .semantics {
                                    contentDescription =
                                        "Modifier le nom de l'annonce ${annonce.nom}"
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "",
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        if (showModifyAnnonceAudioPopup.value) {
                            ModifyAnnoncesBaliseAudioPopup(
                                annonce = balise.annonces[idAnnonceEdit.intValue],
                                balise = balise,
                                navController = navController
                            ) { showModifyAnnonceAudioPopup.value = false }
                        }

                    }

                    OutlinedButton(
                        onClick = {
                            var verif = false
                            for (i in balise.plages ) {
                                if ( annonce == i.nomMessage ) {
                                    verif = true
                                    ToastUtil.showToast(context, "Annonce non supprimable, elle est utilisée dans une plage horaire !")
                                }
                            }
                            if (!verif) {
                                idAnnonceEdit.intValue=balise.annonces.indexOf(annonce)
                                showConfirmDeletePopup.value = true
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
                            .semantics { contentDescription = "Supprimer l'annonce ${annonce.nom}" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    if (showConfirmDeletePopup.value) {
                        ConfirmDeleteAnnoncePopup(
                            balise,
                            annonce = balise.annonces[idAnnonceEdit.intValue],
                            navController = navController
                        ) { showConfirmDeletePopup.value = false }
                    }

                }
            }
        }
    }
}




// Définition de la fonction pour enregistrer le fichier audio localement
fun saveAudioFileLocally(context: Context, bytes: ByteArray, filename: String): File? {
    val file = File(context.cacheDir, filename)
    try {
        FileOutputStream(file).use { outputStream ->
            outputStream.write(bytes)
        }
        return file
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}