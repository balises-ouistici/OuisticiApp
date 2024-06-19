package com.example.ouistici.ui.baliseViewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory class for creating instances of BaliseViewModel.
 *
 * @param application The application context used to create BaliseViewModel.
 */
class BaliseViewModelFactory(private val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
    /**
     * Creates a new instance of the specified ViewModel.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A newly created instance of the specified ViewModel class.
     * @throws IllegalArgumentException if the ViewModel class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaliseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BaliseViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
