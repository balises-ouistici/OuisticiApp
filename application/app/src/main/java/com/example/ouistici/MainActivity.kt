package com.example.ouistici

import android.Manifest.permission.RECORD_AUDIO
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.ouistici.model.AndroidAudioPlayer
import com.example.ouistici.model.AndroidAudioRecorder
import com.example.ouistici.ui.navigation.BottomAppBarExample
import com.example.ouistici.ui.theme.OuisticiTheme


class MainActivity : ComponentActivity() {

    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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



