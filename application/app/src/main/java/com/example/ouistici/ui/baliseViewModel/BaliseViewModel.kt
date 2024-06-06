package com.example.ouistici.ui.baliseViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ouistici.data.entity.BaliseDatabase
import com.example.ouistici.data.entity.BaliseEntity
import com.example.ouistici.data.entity.BaliseRepository
import com.example.ouistici.data.service.RestApiService
import com.example.ouistici.model.Balise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @brief ViewModel for managing the selected balise.
 */
class BaliseViewModel(application: Application) : AndroidViewModel(application) {
    /** The selected balise. */
    var selectedBalise: Balise? = null

    private val repository: BaliseRepository
    val allBalises: LiveData<List<BaliseEntity>>

    init {
        val baliseDao = BaliseDatabase.getDatabase(application).baliseDao()
        repository = BaliseRepository(baliseDao)
        allBalises = repository.allBalises
    }

    fun insert(balise: BaliseEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(balise)
    }

    fun deleteBalise(balise: BaliseEntity) {
        viewModelScope.launch {
            repository.delete(balise)
        }
    }

    fun updateBalise(balise: BaliseEntity) {
        viewModelScope.launch {
            repository.update(balise)
        }
    }

    fun getMaxId(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val maxId = repository.getMaxId()
            callback(maxId)
        }
    }



    fun loadBaliseInfo(balise: BaliseEntity, onResult: (Balise?) -> Unit) {
        val apiService = RestApiService()
        viewModelScope.launch {
            apiService.fetchBaliseInfo(balise) { baliseTemp ->
                if (balise != null && baliseTemp?.id == balise.id) {
                    selectedBalise = baliseTemp
                }
                onResult(baliseTemp)
            }
        }
    }
}