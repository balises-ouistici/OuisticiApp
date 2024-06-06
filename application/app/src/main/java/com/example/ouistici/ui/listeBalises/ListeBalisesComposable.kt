package com.example.ouistici.ui.listeBalises

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.Stub
import com.example.ouistici.model.Balise
import com.example.ouistici.model.ToastUtil
import com.example.ouistici.ui.annonceTexte.getAudioDurationText
import com.example.ouistici.ui.baliseViewModel.BaliseViewModel
import com.example.ouistici.ui.baliseViewModel.RetrofitClient
import com.example.ouistici.ui.loader.Loader
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.TableHeaderColor
import com.example.ouistici.ui.theme.TitleFontFamily
import kotlinx.coroutines.delay
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.RectangleShape
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ouistici.data.dto.AnnonceDto
import com.example.ouistici.data.entity.BaliseEntity
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.Annonce
import com.example.ouistici.ui.baliseViewModel.BaliseViewModelFactory
import com.example.ouistici.ui.theme.BodyBackground


/**
 * @brief Composable function for rendering a list of beacons.
 *
 * @param navController The navigation controller for managing navigation within the app.
 * @param baliseViewModel The view model for managing beacon data.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListeBalises(navController: NavController, baliseViewModel: BaliseViewModel = viewModel()) {
    val balises by baliseViewModel.allBalises.observeAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

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
            text = "Liste des balises Wifi",
            fontSize = 25.sp,
            color = FontColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
        )

        TableScreenWifi(balises, navController, baliseViewModel)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Liste des balises Bluetooth",
            fontSize = 25.sp,
            color = FontColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { contentDescription = "Liste des balises Bluetooth détectées à proximité." }
        )

        TableScreenBluetooth(balises = balises, navController = navController, baliseViewModel = baliseViewModel)

        Spacer(modifier = Modifier.height(50.dp))
        
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Ajouter balise wifi",
                color = Color.White,
                modifier = Modifier.semantics { contentDescription = "Ajouter une balise fonctionnant avec du wifi." }
            )
        }

        if (showDialog) {
            IpPortDialog(
                onDismiss = { showDialog = false },
                onConfirm = { ip, port, name ->
                    baliseViewModel.getMaxId { maxId ->
                        val newBalise = BaliseEntity(
                            id = maxId + 1,
                            nom = name,
                            lieu = "",
                            defaultMessage = null,
                            ipBal = "http://$ip:$port/",
                            volume = 0f,
                            sysOnOff = true,
                            annonces = ArrayList(),
                            plages = ArrayList()
                        )
                        baliseViewModel.insert(newBalise)
                        showDialog = false
                    }
                }
            )
        }

    }
}


@Composable
fun IpPortDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var ip by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Entrez le nom, l'IP et le PORT de la balise",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .semantics {
                            contentDescription = "Défilez à droite pour entrer le nom, l'adresse IP de la balise à ajouter et le port."
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Attention : Le nom de la balise sera seulement enregistré en local et ne correspondra pas forcément au nom de la balise réellement.",
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la balise") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("Adresse IP") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(text = "Annuler", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                            onConfirm(ip, port, name)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text = "Valider", color = Color.White)
                    }
                }
            }
        }
    }
}


@Composable
fun EditBaliseDialog(balise: BaliseEntity, onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    val urlPattern = "http://(.*):(.*)/".toRegex()
    val matchResult = urlPattern.matchEntire(balise.ipBal)
    val (initialIp, initialPort) = if (matchResult != null && matchResult.groupValues.size == 3) {
        matchResult.groupValues[1] to matchResult.groupValues[2]
    } else {
        "" to ""
    }


    var name by remember { mutableStateOf(balise.nom) }
    var ip by remember { mutableStateOf(initialIp) }
    var port by remember { mutableStateOf(initialPort) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Modifier la balise",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .semantics {
                            contentDescription = "Modifier le nom, l'adresse IP et le port de la balise."
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Attention : Le nom de la balise sera seulement enregistré en local et ne correspondra pas forcément au nom de la balise réellement.",
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la balise") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("Adresse IP") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(text = "Annuler", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onConfirm(name, ip, port)
                        onDismiss()
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text = "Valider", color = Color.White)
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConfirmDeleteBalisePopup(
    balise: BaliseEntity,
    navController: NavController,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit // Added a callback for confirming the deletion
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Êtes-vous sûr de vouloir supprimer cette balise ?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = stringResource(R.string.annuler),
                            color = Color.White,
                            modifier = Modifier.semantics { contentDescription = "Annuler la suppression" }
                        )
                    }
                    Button(
                        onClick = {
                            isLoading = true
                            // Call the onConfirm callback to handle deletion
                            onConfirm()
                            isLoading = false
                            onDismiss()
                            navController.navigate("listeBalises")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.supprimer),
                            color = Color.White,
                            modifier = Modifier.semantics { contentDescription = "Confirmer la suppression de la balise" }
                        )
                    }
                    Loader(isLoading = isLoading)
                }
            }
        }
    }
}



/**
 * @brief Composable function for rendering a header cell in a table row.
 *
 * @param text The text content of the header cell.
 * @param weight The weight of the header cell in the table layout.
 * @param textColor The color of the text in the header cell.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TableHeaderCell(
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
            .height(50.dp)
            .semantics { contentDescription = "Titre de colonne du tableau, ${text}." },
        color = textColor,
        textAlign = TextAlign.Center
        )
}


/**
 * @brief Composable function for rendering a cell in a table row.
 *
 * @param text The text content of the cell.
 * @param weight The weight of the cell in the table layout.
 * @param textColor The color of the text in the cell.
 * @param onClick Lambda function to handle click events on the cell, if null cell is not clickable.
 */
@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    textColor: Color,
    textSemantics: String,
    onClick: (() -> Unit)?
) {
    onClick?.let {
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(50.dp)
            .clickable(onClick = it)
    }?.let {
        Box(
            it
    ) {
            Text(
                text = text,
                Modifier
                    .align(Alignment.Center)
                    .semantics { contentDescription = "$textSemantics : $text" },
            color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


/**
 * @brief Composable function for rendering a table screen displaying a list of balises.
 *
 * @param balises The list of balises to display in the table.
 * @param navController The NavController used for navigation.
 * @param baliseViewModel The BaliseViewModel for managing balise data.
 */
@Composable
fun TableScreenWifi(balises: List<BaliseEntity>, navController: NavController, baliseViewModel: BaliseViewModel) {
    val columnWeight = .5f
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) } // Loader state
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedBalise by remember { mutableStateOf<BaliseEntity?>(null) }


    if (showEditDialog && selectedBalise != null) {
        EditBaliseDialog(
            balise = selectedBalise!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, ip, port ->
                // Update the balise with the new values
                selectedBalise?.let {
                    it.nom = name
                    it.ipBal = "http://$ip:$port/"
                    baliseViewModel.updateBalise(it)
                }
            }
        )
    }

    if (showDeleteDialog && selectedBalise != null) {
        ConfirmDeleteBalisePopup(
            balise = selectedBalise!!,
            navController = navController,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                selectedBalise?.let {
                    baliseViewModel.deleteBalise(it)
                    ToastUtil.showToast(context, "Balise supprimée")
                }
                showDeleteDialog = false
            }
        )
    }


    LazyColumn(
        Modifier
            .fillMaxSize()
            .height(230.dp)
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(TableHeaderColor)) {
                TableHeaderCell(text = stringResource(R.string.nom_balise), weight = columnWeight, textColor = Color.Black)
                TableHeaderCell(text = "Actions", weight = columnWeight, textColor = Color.Black)
            }
        }



        items(balises) { balise ->
            Row(Modifier.fillMaxWidth()) {
                TableCell(
                    text = balise.nom,
                    weight = columnWeight,
                    textColor = Color.Black,
                    textSemantics = "Nom de la balise",
                    onClick = {
                        isLoading = true
                        baliseViewModel.loadBaliseInfo(balise) { loadedBalise ->
                            if (loadedBalise != null) {
                                baliseViewModel.selectedBalise = loadedBalise
                                navController.navigate("infosBalise")
                            } else {
                                Log.d("Oui", "Problème")
                                ToastUtil.showToast(context, "Échec de la connexion à la balise")
                            }
                            isLoading = false // Hide loader
                        }
                    }
                )

                // New cell for edit and delete buttons
                Row(
                    Modifier.weight(columnWeight),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedBalise = balise
                            showEditDialog = true
                        },
                        shape = RectangleShape,
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = BodyBackground
                        ),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .height(66.dp)
                            .weight(columnWeight / 2)
                            .border(1.dp, Color.Black)
                            .semantics {
                                contentDescription =
                                    "Modifier la balise ${balise.nom}"
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "",
                            modifier = Modifier.size(15.dp)
                        )
                    }


                    OutlinedButton(
                        onClick = {
                            selectedBalise = balise
                            showDeleteDialog = true
                        },
                        shape = RectangleShape,
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = BodyBackground
                        ),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .height(66.dp)
                            .weight(columnWeight / 2)
                            .border(1.dp, Color.Black)
                            .semantics { contentDescription = "Supprimer la balise ${balise.nom}" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
            Loader(isLoading = isLoading)
        }
    }
}





@Composable
fun TableScreenBluetooth(balises: List<BaliseEntity>, navController: NavController, baliseViewModel: BaliseViewModel) {
    val columnWeight = .3f
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) } // Loader state

    LazyColumn(
        Modifier
            .fillMaxSize()
            .height(230.dp)
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(TableHeaderColor)) {
                TableHeaderCell(text = stringResource(R.string.nom_balise), weight = columnWeight, textColor = Color.Black)
                TableHeaderCell(text = stringResource(R.string.lieu), weight = columnWeight, textColor = Color.Black)
                TableHeaderCell(text = stringResource(R.string.message_d_faut), weight = columnWeight, textColor = Color.Black)
            }
        }

        items(balises) { balise ->
            Row(Modifier.fillMaxWidth()) {
                // Intégrer les cellules avec TableCell pour les balises bluetooth



                Loader(isLoading = isLoading)
            }
        }
    }
}



