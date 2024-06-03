package com.example.ouistici.ui.ajouterAnnonce

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.ui.theme.FontColor
import kotlinx.coroutines.delay

/**
 * @brief Composable function to navigate in other pages that are useful for adding announcements.
 * @param navController The navigation controller to navigate between screens.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AjouterAnnonce(navController: NavController) {
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
        Text(
            text = stringResource(R.string.quel_type_d_annonce),
            fontSize = 25.sp,
            color = FontColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Vocal announcement option
        Text(
            text = stringResource(R.string.vocale),
            fontSize = 15.sp,
            color = FontColor,
            modifier = Modifier.semantics {
                this.invisibleToUser()
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        LargeFloatingActionButton(
            onClick = { navController.navigate("annonceVocal") },
            shape = CircleShape,
            containerColor = Color.Black,
            modifier = Modifier
                .width(80.dp)
                .height(80.dp)
        ) {
            Text(
                text = "GO",
                color = Color.White,
                fontSize = 25.sp,
                modifier = Modifier.semantics {
                    contentDescription = "Créez une annonce grâce à l'enregistreur vocal de votre téléphone."
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Text announcement option
        Text(
            text = stringResource(R.string.texte),
            fontSize = 15.sp,
            color = FontColor,
            modifier = Modifier.semantics {
                this.invisibleToUser()
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        LargeFloatingActionButton(
            onClick = { navController.navigate("annonceTexte") },
            shape = CircleShape,
            containerColor = Color.Black,
            modifier = Modifier
                .width(80.dp)
                .height(80.dp)

        ) {
            Text(
                text = "GO",
                color = Color.White,
                fontSize = 25.sp,
                modifier = Modifier.semantics {
                    contentDescription = "Créez une annonce en écrivant un texte qui sera lu par un synthétiseur vocal."
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Audio file upload option
        Text(
            text = stringResource(R.string.charger_fichier_audio),
            fontSize = 15.sp,
            color = FontColor,
            modifier = Modifier.semantics {
                this.invisibleToUser()
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        LargeFloatingActionButton(
            onClick = { navController.navigate("annonceMptrois") },
            shape = CircleShape,
            containerColor = Color.Black,
            modifier = Modifier
                .width(80.dp)
                .height(80.dp)

        ) {
            Text(
                text = "GO",
                color = Color.White,
                fontSize = 25.sp,
                modifier = Modifier.semantics {
                    contentDescription = "Créez une annonce en chargeant un fichier audio depuis votre téléphone."
                }
            )
        }
    }
}