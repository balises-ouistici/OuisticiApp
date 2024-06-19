package com.example.ouistici.ui.loader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Composable function for displaying a loading indicator.
 *
 * @param isLoading Boolean flag indicating whether to display the loader.
 */
@Composable
fun Loader(
    isLoading: Boolean
) {
    if (isLoading) {
        Dialog(onDismissRequest = { }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }
    }
}