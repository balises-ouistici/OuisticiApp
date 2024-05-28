package com.example.ouistici.ui.annonceTexte

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.dto.FileAnnonceDto
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.Balise
import com.example.ouistici.model.Langue
import com.example.ouistici.model.LangueManager
import com.example.ouistici.model.TextToSpeechManager
import com.example.ouistici.model.TypeAnnonce
import com.example.ouistici.ui.theme.FontColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import kotlin.coroutines.coroutineContext


/**
 * @brief Composable function for text announcements.
 * @param navController The navigation controller to navigate between screens.
 * @param balise The beacon associated with the announcements.
 */
@Composable
fun AnnonceTexte(navController: NavController, balise: Balise) {
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    var textContenu by remember { mutableStateOf(TextFieldValue()) }
    var textValueInput by remember { mutableStateOf("") }
    var textContenuInput by remember { mutableStateOf("") }
    var langueSelectionnee by remember { mutableStateOf(Langue("", "")) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val ttsManager = remember { TextToSpeechManager(context) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.synth_tiseur_vocal),
            fontSize = 25.sp,
            color = FontColor
        )
       
        Spacer(modifier = Modifier.height(40.dp))
       

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
        
        Spacer(modifier = Modifier.height(20.dp))


        TextField(
            value = textContenu,
            onValueChange = {
                textContenu = it
                textContenuInput = it.text
            },
            label = { Text(stringResource(R.string.entrez_le_contenu_qui_sera_lu_par_le_synth_tiseur_vocal)) },
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row (verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.langue_de_la_voix),
                color = FontColor
            )


            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = { expanded = true })
            ) {

                if ( langueSelectionnee.code == "" && langueSelectionnee.nom == "" ) {
                    Text(
                        text = stringResource(R.string.choisir),
                        fontSize = 16.sp,
                        color = FontColor
                    )
                } else {
                    Text(
                        text = langueSelectionnee.getLangueName(),
                        fontSize = 16.sp,
                        color = FontColor
                    )
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    LangueManager.languesDisponibles.forEach { langue ->
                        DropdownMenuItemLangue(
                            langue = langue,
                            onClick = {
                                langueSelectionnee = langue
                                expanded = false
                            }
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(50.dp))

        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                if (textValueInput != "" && textContenuInput != "" && langueSelectionnee.code != "" && langueSelectionnee.nom != "") {
                    val locale = when (langueSelectionnee.code) {
                        "fr" -> Locale.FRENCH
                        "en" -> Locale.ENGLISH
                        else -> Locale.FRENCH
                    }
                    ttsManager.setLanguage(locale)

                    val fileName = balise.createId().toString() + ".wav"
                    val file = File(context.cacheDir, fileName)
                    coroutineScope.launch {

                        withContext(Dispatchers.Default) {
                            ttsManager.saveToFile(textContenuInput, file)
                        }

                    // Utiliser une coroutine pour ajouter un délai de 2 secondes

                        val duration = AndroidAudioPlayer.getAudioDuration(file) / 1000

                        val apiService = RestApiService()
                        val annInfo = AnnonceDto(
                            upload_sound_url = null,
                            id_annonce = balise.createId(),
                            nom = textValueInput,
                            type = TypeAnnonce.TEXTE.toString(),
                            contenu = textContenuInput,
                            langue = langueSelectionnee.code,
                            duree = duration,
                            filename = fileName
                        )

                        apiService.createAnnonce(annInfo) {
                            if (it?.nom != null) {

                                val audioInfo = FileAnnonceDto(
                                    code = null,
                                    value = it.nom,
                                    audiofile = file
                                )
                                apiService.createAudio(audioInfo) {
                                    Log.e("CreateAnnonce", "Échec création d'annonce : $it")

                                    if (it?.code != null) {
                                        balise.annonces.add(
                                            Annonce(
                                                balise.createId(),
                                                textValueInput,
                                                TypeAnnonce.TEXTE,
                                                file,
                                                textContenuInput,
                                                langueSelectionnee,
                                                duration,
                                                balise.createId().toString() + ".wav"
                                            )
                                        )

                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.annonce_ajout_e),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Log.e("CreateAnnonce", "Échec création d'annonce")
                                        Toast.makeText(
                                            context,
                                            "Échec lors de l'envoie du fichier au serveur",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            } else {
                                Log.e("CreateAnnonce", "Échec création d'annonce")
                                Toast.makeText(
                                    context,
                                    "Échec lors de la création de l'annonce",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        navController.navigate("annonceTexte")
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.action_impossible_vous_devez_remplir_les_champs),
                        Toast.LENGTH_LONG
                    ).show()
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


/**
 * @brief Composable function for dropdown menu item representing a language.
 * @param langue The language object.
 * @param onClick The callback function when the item is clicked.
 */
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

fun getAudioDurationText(file: File): Int {
    val mediaPlayer = MediaPlayer()
    mediaPlayer.setDataSource(file.absolutePath)
    mediaPlayer.prepare()
    val duration = mediaPlayer.duration
    mediaPlayer.release()
    return duration / 1000 // Convertit la durée en millisecondes en secondes
}
