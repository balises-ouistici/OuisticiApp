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
import androidx.appcompat.app.AppCompatActivity


/**
 * @brief Main activity of the application.
 *
 * This activity sets up the user interface using Jetpack Compose and handles
 * the initialization of audio recorder and player objects.
 */
class MainActivity : AppCompatActivity() {
    /**
     * @brief Lazy-initialized audio recorder instance.
     *
     * This recorder is used for recording audio in the application.
     */
    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    /**
     * @biref Lazy-initialized audio player instance.
     *
     * This player is used for playing audio in the application.
     */
    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    /**
     * @brief Initializes the activity when it is created.
     *
     * This method sets up the UI using Jetpack Compose, requests necessary
     * permissions, and initializes the audio recorder and player.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
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



