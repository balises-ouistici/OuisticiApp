package com.example.ouistici.model

import android.content.Context
import android.widget.Toast

object ToastUtil {
    private var currentToast: Toast? = null

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
        currentToast?.cancel()
        currentToast = Toast.makeText(context, message, duration)
        currentToast?.show()
    }
}