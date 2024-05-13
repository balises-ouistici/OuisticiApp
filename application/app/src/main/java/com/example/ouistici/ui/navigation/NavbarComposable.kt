package com.example.ouistici.ui.navigation

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomAppBarExample(recorder: AndroidAudioRecorder, player: AndroidAudioPlayer, cacheDir : File) {
    val navController = rememberNavController()
    val baliseViewModel: BaliseViewModel = viewModel()
    val isBottomAppBarVisible = remember { mutableStateOf(true) }


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
                            modifier = Modifier.size(100.dp)
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
                        "Ajouter",
                        Icons.Filled.AddCircle,
                        columnModifier
                    )
                    ClickableColumn(
                        navController,
                        "manageAnnonce",
                        "Modifier",
                        Icons.Filled.Edit,
                        columnModifier
                    )
                    ClickableColumn(
                        navController,
                        "infosBalise",
                        "Infos",
                        Icons.Filled.Info,
                        columnModifier
                    )
                    ClickableColumn(
                        navController,
                        "settings",
                        "Options",
                        Icons.Filled.Settings,
                        columnModifier
                    )
                    ClickableColumn(
                        navController,
                        "listeBalises",
                        "Balises",
                        Icons.Filled.ExitToApp,
                        columnModifier
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
                    AjouterAnnonce(
                        navController = navController
                    )
                }

                composable(route = "manageAnnonce") {
                    val selectedBalise = baliseViewModel.selectedBalise
                    if (selectedBalise != null) {
                        ChoixAnnonce(
                            navController = navController,
                            balise = selectedBalise
                        )
                    }
                }

                composable(route = "infosBalise") { backStackEntry ->
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


@Composable
fun ClickableColumn(navController: NavController, destination: String, text: String, icon: ImageVector, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable {
            navController.navigate(destination)
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = text,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Topbar() {
    val logo = painterResource(id = R.drawable.logo)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logo de l'application") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.height(70.dp)
            )
        }
    ) {
        Image(
            painter = logo,
            contentDescription = "Logo de l'application",
            modifier = Modifier.width(300.dp)
        )
    }
}