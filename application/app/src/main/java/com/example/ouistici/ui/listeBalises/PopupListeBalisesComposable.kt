package com.example.ouistici.ui.listeBalises

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.ouistici.R
import com.example.ouistici.data.entity.BaliseEntity
import com.example.ouistici.ui.loader.Loader

@Composable
fun IpPortDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var ip by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.text_beaconlistpopup_enter_ip_and_port),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .semantics {
                            contentDescription =
                                context.getString(R.string.a11y_beaconlistpopup_enter_ip_and_port)
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.text_beaconlistpopup_warning),
                    modifier = Modifier
                        .semantics {
                            contentDescription =
                                context.getString(R.string.a11y_beaconlistpopup_warning)
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.text_beaconlistpopup_beacon_name)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text(stringResource(R.string.text_beaconlistpopup_ip)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text(stringResource(R.string.text_beaconlistpopup_port)) },
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
                        Text(
                            text = stringResource(R.string.text_beaconlistpopup_cancel_button),
                            color = Color.White,
                            modifier = Modifier
                                .semantics { contentDescription =
                                    context.getString(R.string.a11y_beaconlistpopup_cancel_create_button) }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onConfirm(ip, port, name)
                        onDismiss()
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(
                            text = stringResource(R.string.text_beaconlistpopup_validate_button),
                            color = Color.White,
                            modifier = Modifier
                                .semantics { contentDescription =
                                    context.getString(R.string.a11y_beaconlistpopup_validate_create_button) }
                        )
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

    val context = LocalContext.current
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
                    text = stringResource(R.string.text_beaconlistpopup_modify_beacon),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .semantics {
                            contentDescription =
                                context.getString(R.string.a11y_beaconlistpopup_modify_name_ip_and_port)
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.text_beaconlistpopup_warning),
                    modifier = Modifier
                        .semantics {
                            contentDescription =
                                context.getString(R.string.a11y_beaconlistpopup_warning)
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.text_beaconlistpopup_beacon_name)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text(stringResource(R.string.text_beaconlistpopup_ip)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text(stringResource(R.string.text_beaconlistpopup_port)) },
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
                        Text(
                            stringResource(R.string.text_beaconlistpopup_cancel_button),
                            color = Color.White,
                            modifier = Modifier
                                .semantics { contentDescription =
                                    context.getString(R.string.a11y_beaconlistpopup_cancel_edit_button) }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onConfirm(name, ip, port)
                        onDismiss()
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(
                            text = stringResource(R.string.text_beaconlistpopup_validate_button),
                            color = Color.White,
                            modifier = Modifier
                                .semantics { contentDescription =
                                    context.getString(R.string.a11y_beaconlistpopup_validate_edit_button) }
                        )
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
                    text = stringResource(R.string.text_beaconlistpopup_confirm_delete_beacon),
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
                            modifier = Modifier.semantics { contentDescription =
                                context.getString(R.string.a11y_beaconlistpopup_cancel_deletion) }
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
                            modifier = Modifier.semantics { contentDescription =
                                context.getString(R.string.a11y_beaconlistpopup_confirm_deletion) }
                        )
                    }
                    Loader(isLoading = isLoading)
                }
            }
        }
    }
}