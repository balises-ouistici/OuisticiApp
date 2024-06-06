package com.example.ouistici.ui.annouceAndDaySelector

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ouistici.R
import com.example.ouistici.model.Annonce
import com.example.ouistici.model.JoursSemaine
import java.util.Locale

/**
 * @brief Composable function for displaying a list of announcements.
 *
 * @param annonces List of announcements to display.
 * @param selectedAnnonce Currently selected announcement.
 * @param onAnnonceSelected Callback function invoked when an announcement is selected.
 */
@Composable
fun AnnonceList(
    annonces: List<Annonce>,
    selectedAnnonce: Annonce?,
    onAnnonceSelected: (Annonce) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .height(100.dp)
    ) {
        items(annonces) { annonce ->
            val isSelected = annonce == selectedAnnonce
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAnnonceSelected(annonce) }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = annonce.nom,
                        modifier = Modifier.semantics {
                            contentDescription = "Nom de l'annonce, ${annonce.nom}."
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Green // Change the color as desired
                        )
                    }
                }
            }
        }
    }
}


/**
 * @brief Composable function for displaying a list of messages to select a default message in another component.
 *
 * @param annonces List of default messages to display.
 * @param selectedAnnonce Currently selected default message.
 * @param onAnnonceSelected Callback function invoked when a default message is selected.
 */
@Composable
fun AnnonceDefaultMessageList(
    annonces: List<Annonce>,
    selectedAnnonce: Annonce?,
    onAnnonceSelected: (Annonce?) -> Unit
) {
    if (annonces.isEmpty()) {
        Text(
            text = stringResource(R.string.cr_ez_d_abord_une_annonce),
            modifier = Modifier.semantics {
                contentDescription = "Vous n'avez pas encore créé d'annonce, veuillez en créer une dans un premier temps."
            }
        )
    } else {
        LazyColumn(
            modifier = Modifier.height(200.dp)
        ) {
            items(annonces) { annonce ->
                val isSelected = annonce == selectedAnnonce
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAnnonceSelected(if (isSelected) null else annonce) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = annonce.nom,
                        modifier = Modifier
                            .weight(1f)
                            .semantics { contentDescription = "Nom de l'annonce, ${annonce.nom}." },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Green // Change the color as desired
                        )
                    }
                }
            }
        }
    }
}



/**
 * @brief Composable function for selecting days of the week.
 *
 * @param selectedJours Currently selected days of the week.
 * @param onJoursSelected Callback function invoked when days of the week are selected.
 */
@SuppressLint("DiscouragedApi")
@Composable
fun JoursSemaineSelector(
    selectedJours: List<JoursSemaine>,
    onJoursSelected: (List<JoursSemaine>) -> Unit
) {
    val joursSemaine = JoursSemaine.values().toList()

    val context = LocalContext.current
    val resources = context.resources

    Box(
        modifier = Modifier
            .height(200.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .width(300.dp)
        ) {
            for (jour in joursSemaine) {
                val isChecked = selectedJours.contains(jour)
                val onClick = {
                    val updatedList = if (isChecked) {
                        selectedJours - jour
                    } else {
                        selectedJours + jour
                    }
                    onJoursSelected(updatedList)
                }

                val stringResourceId = resources.getIdentifier(jour.name.lowercase(Locale.ROOT), "string", context.packageName)
                val jourName = if (stringResourceId != 0) {
                    resources.getString(stringResourceId)
                } else {
                    jour.name // Utilise le nom de l'enum si la ressource n'est pas trouvée
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onClick() }
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = null,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = jourName, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}