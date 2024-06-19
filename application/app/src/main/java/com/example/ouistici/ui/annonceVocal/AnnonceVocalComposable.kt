package com.example.ouistici.ui.annonceVocal

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.FileAnnonceDto
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.AndroidAudioRecorder
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.ToastUtil
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.ui.loader.Loader
import com.example.ouistici.ui.theme.FontColor
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * @brief Composable function for audio recorder announcements.
 * @param navController The navigation controller to navigate between screens.
 * @param recorder The recorder for the microphone.
 * @param player The audio player.
 * @param cacheDir Where the temp file created by the audio recorder temporary save the temp file.
 * @param balise The beacon associated with the announcements.
 */
@SuppressLint("StringFormatMatches")
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

    var isLoading by remember { mutableStateOf(false) }

    val audioFile = File(cacheDir, balise.createId().toString()+".wav")

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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                if (audioFile != null) {
                    recorder.start(audioFile)
                }

                startTime = System.currentTimeMillis() - time
                isRunning = true
                currentStep = 2
            } else {
                ToastUtil.showToast(context,
                    context.getString(R.string.toast_vocalfile_recording_unauthorized))
            }
        }
    )


    val keyboardController = LocalSoftwareKeyboardController.current

    val focusRequester = remember { FocusRequester() }
    val view = LocalView.current
    LaunchedEffect(Unit) {
        delay(100) // Add a slight delay to ensure the screen is fully loaded
        focusRequester.requestFocus()
        view.announceForAccessibility("")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.text_vocalfile_title),
            fontSize = 25.sp,
            color = FontColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .semantics {
                    contentDescription = context.getString(R.string.a11y_vocalfile_title)
                }
                .focusRequester(focusRequester)
                .focusable()
        )
        Spacer(modifier = Modifier.padding(10.dp))
        when (currentStep) {
            1 -> {
                LargeFloatingActionButton(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            if (audioFile != null) {
                                recorder.start(audioFile)
                            }

                            startTime = System.currentTimeMillis() - time
                            isRunning = true
                            keyboardController?.hide()
                            currentStep = 2
                        } else {
                            launcher.launch(Manifest.permission.RECORD_AUDIO)
                        }
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
                        modifier = Modifier
                            .padding(9.dp)
                            .semantics {
                                contentDescription = context.getString(
                                    R.string.a11y_vocalfile_announcement_duration,
                                    TimeUnit.MILLISECONDS.toMinutes(time) % 60,
                                    TimeUnit.MILLISECONDS.toSeconds(time) % 60
                                )
                            }
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
                        contentDescription = stringResource(R.string.a11y_vocalfile_restart_recording),
                        modifier = Modifier.size(70.dp)
                    )
                }
                Row {
                    Button(
                        onClick = {
                            player.playFile(audioFile)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.semantics { contentDescription =
                            context.getString(R.string.a11y_vocalfile_play_button) }
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
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.a11y_vocalfile_stop_button)
                        }
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
                    modifier = Modifier
                        .padding(9.dp)
                        .semantics {
                            contentDescription =
                                context.getString(
                                    R.string.a11y_vocalfile_announcement_duration,
                                    TimeUnit.MILLISECONDS.toMinutes(time) % 60,
                                    TimeUnit.MILLISECONDS.toSeconds(time) % 60
                                )
                        }
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

                            isLoading = true
                            val apiService = RestApiService()
                            val annInfo = AnnonceDto(
                                upload_sound_url = null,
                                id_annonce = balise.createId(),
                                nom = textValueInput,
                                type = TypeAnnonce.AUDIO.toString(),
                                contenu = "",
                                langue = "",
                                duree = durationInSeconds.toInt(),
                                filename = audioFile.name
                            )

                            apiService.createAnnonce(annInfo) {
                                if ( it?.nom != null ) {

                                    val audioInfo = FileAnnonceDto(
                                        code = null,
                                        value = it.nom,
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
                                                    durationInSeconds.toInt(),
                                                    audioFile.name
                                                )
                                            )

                                            ToastUtil.showToast(context,
                                                context.getString(R.string.toast_vocalfile_announcement_added))
                                        } else {
                                            Log.e("CreateAnnonce","Échec création d'annonce")
                                            ToastUtil.showToast(context,
                                                context.getString(R.string.toast_vocalfile_failure_sending_file))
                                        }
                                    }
                                } else {
                                    Log.e("CreateAnnonce","Échec création d'annonce")
                                    ToastUtil.showToast(context,
                                        context.getString(R.string.toast_vocalfile_failure_creating_announcement))
                                }
                            }
                            isLoading = false
                            navController.navigate("annonceVocal")
                        } else {
                            ToastUtil.showToast(context,
                                context.getString(R.string.toast_vocalfile_impossible_must_enter_name))
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(
                        text = stringResource(R.string.enregistrer),
                        color = Color.White,
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.a11y_vocalfile_save_announcement)
                        }
                    )
                }
                Loader(isLoading = isLoading)

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