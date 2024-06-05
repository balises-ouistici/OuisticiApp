package com.example.ouistici.ui.baliseViewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BaliseViewModelFactory(private val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaliseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BaliseViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
