package com.example.ouistici.ui.listeBalises

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ouistici.ui.baliseViewModel.BaliseViewModel
import com.example.ouistici.ui.theme.FontColor
import kotlinx.coroutines.delay
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ouistici.R
import com.example.ouistici.data.entity.BaliseEntity


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
    val context = LocalContext.current
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
            text = stringResource(R.string.text_beaconlist_title_wifi),
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
            text = stringResource(R.string.text_beaconlist_title_bluetooth),
            fontSize = 25.sp,
            color = FontColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { contentDescription =
                context.getString(R.string.a11y_beaconlist_title_bluetooth) }
        )

        TableScreenBluetooth(balises = balises, navController = navController, baliseViewModel = baliseViewModel)

        Spacer(modifier = Modifier.height(50.dp))
        
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = stringResource(R.string.text_beaconlist_add_wifi_beacon),
                color = Color.White,
                modifier = Modifier.semantics { contentDescription =
                    context.getString(R.string.a11y_beaconlist_add_wifi_beacon) }
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










