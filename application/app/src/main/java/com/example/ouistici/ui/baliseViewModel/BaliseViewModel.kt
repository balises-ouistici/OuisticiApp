package com.example.ouistici.ui.baliseViewModel

import androidx.lifecycle.ViewModel
import com.example.ouistici.model.Balise

/**
 * @brief ViewModel for managing the selected balise.
 */
class BaliseViewModel : ViewModel() {
    /** The selected balise. */
    var selectedBalise : Balise? = null
}