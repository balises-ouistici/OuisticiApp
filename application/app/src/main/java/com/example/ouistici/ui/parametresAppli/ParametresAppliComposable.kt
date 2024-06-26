package com.example.ouistici.ui.parametresAppli

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.model.Langue
import com.example.ouistici.model.LangueManager
import com.example.ouistici.ui.theme.FontColor
import kotlinx.coroutines.delay
import java.util.Locale


/**
 * @brief Composable function for rendering application settings.
 *
 * @param navController The navigation controller.
 */
@Composable
fun ParametresAppli(navController: NavController) {
    val context = LocalContext.current

    // Accessibilité
    val focusRequester = remember { FocusRequester() }
    val view = LocalView.current
    LaunchedEffect(Unit) {
        delay(100) // Add a slight delay to ensure the screen is fully loaded
        focusRequester.requestFocus()
        view.announceForAccessibility("")
    }

    // Autre
    val availableLocales = listOf("fr", "en") // Liste des langues disponibles

    var expanded by remember { mutableStateOf(false) }
    var selectedLocale by remember { mutableStateOf(LangueManager.langueActuelle.code) }

    val onClickRefreshActivity = {
        context.findActivity()?.runOnUiThread {
            val appLocale = LocaleListCompat.forLanguageTags(selectedLocale)
            AppCompatDelegate.setApplicationLocales(appLocale)
            if ( selectedLocale == "fr" ) LangueManager.langueActuelle = Langue("fr","Français")
            else LangueManager.langueActuelle = Langue("en", "English")
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.parametres_de_l_application),
            fontSize = 25.sp,
            color = FontColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .semantics {
                    contentDescription = context.getString(R.string.a11y_settings_title)
                }
                .focusRequester(focusRequester)
                .focusable()
        )

        Box(modifier = Modifier.padding(top = 16.dp)) {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) { // Changement ici
                Text(
                    text = stringResource(R.string.changer_de_langue),
                    color = Color.White,
                    modifier = Modifier.semantics { contentDescription =
                        context.getString(R.string.a11y_settings_change_language) }
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableLocales.forEach { locale ->
                    DropdownMenuItem(onClick = {
                        selectedLocale = locale
                        onClickRefreshActivity()
                        expanded = false
                    }) {
                        Text(text = Locale(locale).displayName)
                    }
                }
            }
        }
    }
}


/**
 * @brief Composable function for rendering a dropdown menu item.
 *
 * @param onClick The callback function to be invoked when the item is clicked.
 * @param content The content to be displayed within the item.
 */
@Composable
fun DropdownMenuItem(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        content()
    }
}


/**
 * @brief Extension function to find the activity associated with a context.
 *
 * @return The activity associated with the context, or null if no activity is found.
 */
fun Context.findActivity() : Activity? = when(this){
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}




