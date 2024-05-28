package com.example.ouistici.ui.baliseViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.Balise
import kotlinx.coroutines.launch

/**
 * @brief ViewModel for managing the selected balise.
 */
class BaliseViewModel : ViewModel() {
    /** The selected balise. */
    var selectedBalise: Balise? = null

    fun loadBaliseInfo(baliseId: Int, onResult: (Balise?) -> Unit) {
        val apiService = RestApiService()
        viewModelScope.launch {
            apiService.fetchBaliseInfo { balise ->
                if (balise != null && balise.id == baliseId) {
                    selectedBalise = balise
                }
                onResult(balise)
            }
        }
    }
}