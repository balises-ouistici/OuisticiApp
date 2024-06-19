package com.example.ouistici.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ouistici.R
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.AndroidAudioRecorder
import com.example.ouistici.ui.ajouterAnnonce.AjouterAnnonce
import com.example.ouistici.ui.annonceMptrois.AnnonceMptrois
import com.example.ouistici.ui.annonceTexte.AnnonceTexte
import com.example.ouistici.ui.annonceVocal.AnnonceVocale
import com.example.ouistici.ui.baliseViewModel.BaliseViewModel
import com.example.ouistici.ui.choixAnnonce.ChoixAnnonce
import com.example.ouistici.ui.infosBalise.InfosBalise
import com.example.ouistici.ui.listeBalises.ListeBalises
import com.example.ouistici.ui.parametresAppli.ParametresAppli
import com.example.ouistici.ui.theme.BodyBackground
import com.example.ouistici.ui.theme.NavBackground
import java.io.File


/**
 * @brief Composable function for rendering a bottom app bar a top app bar and navigation between pages.
 *
 * @param recorder The AndroidAudioRecorder instance.
 * @param player The AndroidAudioPlayer instance.
 * @param cacheDir The cache directory file.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BottomAppBarExample(recorder: AndroidAudioRecorder, player: AndroidAudioPlayer, cacheDir : File) {
    val navController = rememberNavController()
    val baliseViewModel: BaliseViewModel = viewModel()
    val isBottomAppBarVisible = remember { mutableStateOf(true) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo de l'application",
                            modifier = Modifier
                                .size(100.dp)
                                .semantics {
                                    this.invisibleToUser()
                                }
                        )
                    }
                },
            )
        },
        bottomBar = {
            if (isBottomAppBarVisible.value) {
                BottomAppBar(
                    containerColor = NavBackground,
                    contentColor = Color.Black,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(70.dp),

                    ) {
                    val columnModifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()

                    ClickableColumn(
                        navController,
                        "addAnnonce",
                        stringResource(R.string.ajouter),
                        Icons.Filled.AddCircle,
                        columnModifier,
                        "une annonce.",
                        currentRoute == "addAnnonce"
                    )
                    ClickableColumn(
                        navController,
                        "manageAnnonce",
                        stringResource(R.string.modifier),
                        Icons.Filled.Edit,
                        columnModifier,
                        "les plages horaires.",
                        currentRoute == "manageAnnonce"
                    )
                    ClickableColumn(
                        navController,
                        "infosBalise",
                        stringResource(R.string.infos),
                        Icons.Filled.Info,
                        columnModifier,
                        "de la balise.",
                        currentRoute == "infosBalise"
                    )
                    ClickableColumn(
                        navController,
                        "settings",
                        stringResource(R.string.options),
                        Icons.Filled.Settings,
                        columnModifier,
                        "de l'application.",
                        currentRoute == "settings"
                    )
                    ClickableColumn(
                        navController,
                        "listeBalises",
                        stringResource(R.string.balises),
                        Icons.Filled.ExitToApp,
                        columnModifier,
                        "liste des balises.",
                        currentRoute == "listeBalises"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BodyBackground)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "listeBalises"
            ) {

                // ROUTES DE LA NAVBAR

                composable(route = "addAnnonce") {
                    isBottomAppBarVisible.value = true
                    AjouterAnnonce(
                        navController = navController
                    )
                }

                composable(route = "manageAnnonce") {
                    val selectedBalise = baliseViewModel.selectedBalise
                    if (selectedBalise != null) {
                        isBottomAppBarVisible.value = true
                        ChoixAnnonce(
                            navController = navController,
                            balise = selectedBalise
                        )
                    }
                }

                composable(route = "infosBalise") {
                    val selectedBalise = baliseViewModel.selectedBalise
                    if (selectedBalise != null) {
                        isBottomAppBarVisible.value = true
                        InfosBalise(
                            navController = navController,
                            balise = selectedBalise,
                            player = player
                        )
                    }
                }

                composable(route = "settings") {
                    isBottomAppBarVisible.value = true
                    ParametresAppli(
                        navController = navController
                    )
                }

                composable(route = "listeBalises") {
                    isBottomAppBarVisible.value = false
                    ListeBalises(
                        navController = navController,
                        baliseViewModel = baliseViewModel
                    )
                }

                // ROUTES AJOUT ANNONCE

                composable(route = "annonceVocal") {
                    val selectedBalise = baliseViewModel.selectedBalise
                    if (selectedBalise != null) {
                        isBottomAppBarVisible.value = true
                        AnnonceVocale(
                            navController = navController,
                            recorder = recorder,
                            player = player,
                            cacheDir = cacheDir,
                            balise = selectedBalise
                        )
                    }
                }

                composable(route = "annonceTexte") {
                    val selectedBalise = baliseViewModel.selectedBalise
                    if (selectedBalise != null) {
                        isBottomAppBarVisible.value = true
                        AnnonceTexte(
                            navController = navController,
                            balise = selectedBalise
                        )
                    }
                }

                composable(route = "annonceMptrois") {
                    val selectedBalise = baliseViewModel.selectedBalise
                    if (selectedBalise != null) {
                        isBottomAppBarVisible.value = true
                        AnnonceMptrois(
                            navController = navController,
                            player = player,
                            balise = selectedBalise
                        )
                    }
                }
            }
        }
    }
}


/**
 * @brief Composable function for rendering a clickable column.
 *
 * @param navController The navigation controller.
 * @param destination The destination route.
 * @param text The text to display.
 * @param icon The icon to display.
 * @param modifier The modifier for the column.
 */
@Composable
fun ClickableColumn(
    navController: NavController,
    destination: String,
    text: String,
    icon: ImageVector,
    modifier: Modifier,
    textSemantics: String,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) Color.Gray else Color.Transparent
    val textColor = if (isSelected) Color.White else Color.Black

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(backgroundColor)
            .clickable {
                navController.navigate(destination)
            }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = textColor
        )
        Text(
            text = text,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier
                .padding(top = 4.dp)
                .semantics {
                    contentDescription = "Barre de navigation, $text $textSemantics"
                }
        )
    }
}