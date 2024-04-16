package com.example.ouistici

import android.Manifest.permission.RECORD_AUDIO
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.AndroidAudioRecorder
import com.example.ouistici.ui.navigation.BottomAppBarExample
import com.example.ouistici.ui.parametresAppli.ParametresAppli
import com.example.ouistici.ui.theme.FontColor
import com.example.ouistici.ui.theme.OuisticiTheme
import java.io.File


class MainActivity : ComponentActivity() {

    val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(RECORD_AUDIO), 0
        )

        setContent {
            OuisticiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    BottomAppBarExample(recorder, player, cacheDir)
                }
            }
        }
    }
}



