package com.example.ouistici.ui.annonceMptrois

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { uriNotNull ->
            val file = getFileFromUri(uriNotNull, context)
            audioFile = file
        }
    }



    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Choisir fichier audio :",
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



        Spacer(modifier = Modifier.height(50.dp))


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
        Button(
            onClick = {
                if ( textValueInput != "" && audioFile != null ) {
                    balise.annonces.add(Annonce(balise.createId(), textValueInput, TypeAnnonce.AUDIO, audioFile, null, null))
                    Toast.makeText(
                        context,
                        "Annonce ajoutée",
                        Toast.LENGTH_LONG)
                        .show()
                    navController.navigate("annonceMptrois")
                }
                if ( audioFile == null && textValueInput == "" ) {
                    Toast.makeText(
                        context,
                        "Action impossible, vous devez sélectionner un audio et saisir un nom",
                        Toast.LENGTH_LONG)
                        .show()
                }
                if ( audioFile == null && textValueInput != "" ) {
                    Toast.makeText(
                        context,
                        "Action impossible, vous devez sélectionner un audio",
                        Toast.LENGTH_LONG)
                        .show()
                }
                if ( audioFile != null && textValueInput == "" ) {
                    Toast.makeText(
                        context,
                        "Action impossible, vous devez saisir un nom",
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


private fun getFileFromUri(uri: Uri, context: Context): File? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val outputFile = File.createTempFile("temp", null, context.cacheDir)
    inputStream?.use { input ->
        FileOutputStream(outputFile).use { output ->
            input.copyTo(output)
        }
    }
    return outputFile
}


