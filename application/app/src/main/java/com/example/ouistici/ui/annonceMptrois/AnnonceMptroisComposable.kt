package com.example.ouistici.ui.annonceMptrois

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.example.ouistici.ui.theme.FontColor
import java.io.File

@Composable
fun AnnonceMptrois(navController: NavController, player: AndroidAudioPlayer, cacheDir : File) {
    var textValue by remember { mutableStateOf(TextFieldValue()) }

    val result = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        result.value = it
    }

    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Choisir fichier mp3 :",
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

        val audioFile = File(cacheDir, result.toString())

        result.value?.let { audio ->
            Text(
                color = Color.Black,
                text = "Adresse fichier sélectionné : \"" + audio.path.toString() + "\""
            )
        }




        Row {
            Button(
                onClick = {
                    // player.playFile(audioFile)
                          mediaPlayer?.start()

                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play arrow")
            }
            Button(
                onClick = {
                    // player.stop()
                    mediaPlayer?.stop()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(
                    text = "||"
                )
            }
        }



        Spacer(modifier = Modifier.height(50.dp))


        TextField(
            value = textValue,
            onValueChange = {
                textValue = it
            },
            label = { Text("Entrez le nom") },
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth()
        )
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


        DisposableEffect(Unit) {
            result.value?.let { uri ->
                Log.d("AnnonceMptrois", "URI du fichier audio : $uri")
                // Créer un MediaPlayer lorsque l'URI est défini
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(context, uri)
                    mediaPlayer?.setOnCompletionListener { mediaPlayer ->
                        // Libérer le MediaPlayer lorsqu'il a terminé de lire
                        mediaPlayer.release()
                    }
                    Log.d("AnnonceMptrois", "MediaPlayer créé")
                }
            }

            onDispose {
                // Libérer le MediaPlayer lorsqu'il n'est plus nécessaire
                mediaPlayer?.release()
                Log.d("AnnonceMptrois", "MediaPlayer libéré")
            }
        }

    }
}
