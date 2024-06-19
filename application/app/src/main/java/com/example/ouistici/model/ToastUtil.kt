package com.example.ouistici.model

import android.content.Context
import android.widget.Toast


/**
 * Utility object for displaying toast messages.
 */
object ToastUtil {
    private var currentToast: Toast? = null

    /**
     * Displays a toast message with the specified text and duration.
     *
     * @param context The context to use. Usually an Activity or Application context.
     * @param message The message to be displayed.
     * @param duration The duration for which the toast should appear. Default is Toast.LENGTH_LONG.
     */
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
        currentToast?.cancel()
        currentToast = Toast.makeText(context, message, duration)
        currentToast?.show()
    }
}