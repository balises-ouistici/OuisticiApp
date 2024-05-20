package com.example.ouistici.ui.annonceVocal

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.BaliseDto
import com.example.ouistici.data.dto.FileAnnonceDto
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.AndroidAudioRecorder
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.ui.theme.FontColor
import kotlinx.coroutines.delay
import java.io.File
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


/**
 * @brief Composable function for audio recorder announcements.
 * @param navController The navigation controller to navigate between screens.
 * @param recorder The recorder for the microphone.
 * @param player The audio player.
 * @param cacheDir Where the temp file created by the audio recorder temporary save the temp file.
 * @param balise The beacon associated with the announcements.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnnonceVocale(
    navController: NavController,
    recorder: AndroidAudioRecorder,
    player: AndroidAudioPlayer,
    cacheDir : File,
    balise: Balise
) {
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    var textValueInput by remember { mutableStateOf("") }

    val compteur : Int = balise.annonces.count()


    val audioFile = File(cacheDir, balise.nom+"-"+compteur+".mp3")

    var currentStep by remember { mutableStateOf(1) }

    var time by remember {
        mutableLongStateOf(0L)
    }

    var isRunning by remember {
        mutableStateOf(false)
    }

    var startTime by remember {
        mutableLongStateOf(0L)
    }

    val context = LocalContext.current

    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.enregistrer_annonce),
            fontSize = 25.sp,
            color = FontColor
        )
        Spacer(modifier = Modifier.padding(10.dp))
        when (currentStep) {
            1 -> {
                LargeFloatingActionButton(
                    onClick = {
                        if (audioFile != null) {
                            recorder.start(audioFile)
                        }


                        startTime = System.currentTimeMillis() - time
                        isRunning = true
                        keyboardController?.hide()


                        currentStep = 2
                    },
                    shape = CircleShape,
                    containerColor = Color.Gray,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.micro),
                        contentDescription = stringResource(R.string.commencer_l_enregistrement),
                        modifier = Modifier.size(70.dp)
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
                    modifier = Modifier.fillMaxWidth(),
                )


            }
            2 -> {
                LargeFloatingActionButton(
                    onClick = {
                        recorder.stop()
                        isRunning = false
                        currentStep = 3
                    },
                    shape = CircleShape,
                    containerColor = Color.Gray,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.croix),
                        contentDescription = stringResource(R.string.arr_ter_l_enregistrement),
                        modifier = Modifier.size(70.dp)
                    )
                }
                // Durée qui change pdnt enregistrement
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = formatTime(timeMi = time),
                        style = MaterialTheme.typography.headlineLarge,
                        color = FontColor,
                        modifier = Modifier.padding(9.dp)
                    )
                    LaunchedEffect(isRunning) {
                        while (isRunning) {
                            delay(1000)
                            time = System.currentTimeMillis()-startTime

                        }
                    }
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
                    modifier = Modifier.fillMaxWidth(),
                )


            }

            3 -> {
                LargeFloatingActionButton(
                    onClick = {
                        if (audioFile != null) {
                            recorder.start(audioFile)
                        }

                        time = 0
                        startTime = System.currentTimeMillis() - time
                        isRunning = true
                        keyboardController?.hide()

                        currentStep = 2
                    },
                    shape = CircleShape,
                    containerColor = Color.Gray,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.micro),
                        contentDescription = stringResource(R.string.commencer_l_enregistrement),
                        modifier = Modifier.size(70.dp)
                    )
                }
                Row {
                    Button(
                        onClick = {
                            player.playFile(audioFile)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play arrow")
                    }
                    Button(
                        onClick = {
                            player.stop()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(
                            text = "||"
                        )
                    }
                }

                Text(
                    text = formatTime(timeMi = time),
                    style = MaterialTheme.typography.headlineLarge,
                    color = FontColor,
                    modifier = Modifier.padding(9.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))


                TextField(
                    value = textValue,
                    onValueChange = {
                        textValue = it
                        textValueInput = it.text
                    },
                    label = { Text(stringResource(R.string.entrez_le_nom)) },
                    textStyle = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    onClick = {
                        if ( textValueInput != "") {
                            val durationInSeconds = TimeUnit.MILLISECONDS.toSeconds(time)

                            val apiService = RestApiService()
                            val annInfo = AnnonceDto(
                                upload_sound_url = null,
                                id_annonce = balise.createId(),
                                nom = textValueInput,
                                type = TypeAnnonce.AUDIO.toString(),
                                contenu = "",
                                langue = "",
                                duree = durationInSeconds.toInt()
                            )

                            apiService.createAnnonce(annInfo) {
                                if ( it?.upload_sound_url != null ) {

                                    val audioInfo = FileAnnonceDto(
                                        code = null,
                                        value = it.upload_sound_url,
                                        audiofile = audioFile
                                    )
                                    apiService.createAudio(audioInfo) {
                                        Log.e("CreateAnnonce","Échec création d'annonce : $it")

                                        if ( it?.code != null ) {
                                            balise.annonces.add(
                                                Annonce(balise.createId(),
                                                    textValueInput,
                                                    TypeAnnonce.AUDIO,
                                                    audioFile,
                                                    null,
                                                    null,
                                                    durationInSeconds.toInt())
                                            )

                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.annonce_ajout_e),
                                                Toast.LENGTH_LONG)
                                                .show()
                                        } else {
                                            Log.e("CreateAnnonce","Échec création d'annonce")
                                            Toast.makeText(
                                                context,
                                                "Échec lors de l'envoie du fichier au serveur",
                                                Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    }
                                } else {
                                    Log.e("CreateAnnonce","Échec création d'annonce")
                                    Toast.makeText(
                                        context,
                                        "Échec lors de la création de l'annonce",
                                        Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            navController.navigate("annonceVocal")
                        } else {
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
    }
}


/**
 * @brief Formats the time into minutes and seconds.
 * @param timeMi The time in milliseconds.
 * @return A formatted string representing the time in "MM:SS" format.
 */
@Composable
fun formatTime(timeMi: Long) : String {
    val min = TimeUnit.MILLISECONDS.toMinutes(timeMi) % 60
    val sec = TimeUnit.MILLISECONDS.toSeconds(timeMi) % 60

    return String.format("%02d:%02d", min, sec)
}