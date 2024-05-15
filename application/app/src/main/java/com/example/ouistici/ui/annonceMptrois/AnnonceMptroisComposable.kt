package com.example.ouistici.ui.annonceMptrois

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.ui.theme.FontColor
import java.io.File
import java.io.FileOutputStream

@Composable
fun AnnonceMptrois(navController: NavController, player: AndroidAudioPlayer, balise: Balise) {
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    var textValueInput by remember { mutableStateOf("") }

    var audioFile by remember { mutableStateOf<File?>(null) }

    val context = LocalContext.current

    var duration by remember { mutableStateOf(0) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { uriNotNull ->
            val file = getFileFromUri(uriNotNull, context)
            val durationInSeconds = file?.let { getAudioDuration(it) }
            if (durationInSeconds != null) {
                duration = durationInSeconds
            }
            audioFile = file
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.choisir_fichier_audio),
            fontSize = 25.sp,
            color = FontColor
        )
        Spacer(modifier = Modifier.padding(10.dp))
        LargeFloatingActionButton(
            onClick = {
                launcher.launch(arrayOf("audio/*"))
            },
            shape = CircleShape,
            containerColor = Color.Black,
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)

        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 25.sp
            )
        }

        Row {
            Button(
                onClick = {
                    audioFile?.let { player.playFile(it) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play arrow")
            }
            Button(
                onClick = {
                    player.stop()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(text = "||")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (audioFile != null) {
            audioFile?.let { file ->
                val fileName = file.name.substringBefore('.')
                Text(
                    text = stringResource(R.string.fichier_s_lectionn, fileName),
                    color = FontColor
                )
            }
            if (duration > 0) {
                Text(
                    text = stringResource(R.string.dur_e_ms, duration / 60, duration % 60),
                    color = FontColor
                )
            } else {
                Text(
                    text = stringResource(R.string.dur_e_probl_me),
                    color = FontColor
                )
            }

        } else {
            Text(
                text = stringResource(R.string.fichier_s_lectionn_aucun),
                color = FontColor
            )
        }


        Spacer(modifier = Modifier.height(50.dp))


        TextField(
            value = textValue,
            onValueChange = {
                textValue = it
                textValueInput = it.text
            },
            label = { Text(stringResource(R.string.entrez_le_nom)) },
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if ( textValueInput != "" && audioFile != null ) {
                    balise.annonces.add(Annonce(balise.createId(), textValueInput, TypeAnnonce.AUDIO, audioFile, null, null, duration))
                    Toast.makeText(
                        context,
                        context.getString(R.string.annonce_ajout_e),
                        Toast.LENGTH_LONG)
                        .show()
                    navController.navigate("annonceMptrois")
                }
                if ( audioFile == null && textValueInput == "" ) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.action_impossible_vous_devez_s_lectionner_un_audio_et_saisir_un_nom),
                        Toast.LENGTH_LONG)
                        .show()
                }
                if ( audioFile == null && textValueInput != "" ) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.action_impossible_vous_devez_s_lectionner_un_audio),
                        Toast.LENGTH_LONG)
                        .show()
                }
                if ( audioFile != null && textValueInput == "" ) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.action_impossible_vous_devez_saisir_un_nom),
                        Toast.LENGTH_LONG)
                        .show()
                }
            },
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


private fun getFileFromUri(uri: Uri, context: Context): File? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val fileName = getFileName(uri, context)
    val outputFile = File.createTempFile(fileName, null, context.cacheDir)
    inputStream?.use { input ->
        FileOutputStream(outputFile).use { output ->
            input.copyTo(output)
        }
    }
    return outputFile
}

@SuppressLint("Range")
private fun getFileName(uri: Uri, context: Context): String {
    var fileName = ""
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return fileName
}

fun getAudioDuration(file: File): Int {
    val mediaPlayer = MediaPlayer()
    mediaPlayer.setDataSource(file.absolutePath)
    mediaPlayer.prepare()
    val duration = mediaPlayer.duration
    mediaPlayer.release()
    return duration / 1000 // Convertit la durée en millisecondes en secondes
}


