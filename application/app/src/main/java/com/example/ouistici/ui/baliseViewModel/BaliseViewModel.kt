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
 * ViewModel for managing Balise data.
 *
 * @param application The application context, typically used for database initialization.
 */
class BaliseViewModel(application: Application) : AndroidViewModel(application) {
    /** The selected balise. */
    var selectedBalise: Balise? = null

    private val repository: BaliseRepository
    val allBalises: LiveData<List<BaliseEntity>>

    init {
        // Initialize repository using the application's context
        val baliseDao = BaliseDatabase.getDatabase(application).baliseDao()
        repository = BaliseRepository(baliseDao)
        allBalises = repository.allBalises
    }

    /**
     * Inserts a new BaliseEntity into the database.
     *
     * @param balise The BaliseEntity to insert.
     */
    fun insert(balise: BaliseEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(balise)
    }

    /**
     * Deletes a BaliseEntity from the database.
     *
     * @param balise The BaliseEntity to delete.
     */
    fun deleteBalise(balise: BaliseEntity) {
        viewModelScope.launch {
            repository.delete(balise)
        }
    }

    /**
     * Updates an existing BaliseEntity in the database.
     *
     * @param balise The BaliseEntity to update.
     */
    fun updateBalise(balise: BaliseEntity) {
        viewModelScope.launch {
            repository.update(balise)
        }
    }

    /**
     * Retrieves the maximum ID present in the database asynchronously.
     *
     * @param callback Callback function with the maximum ID.
     */
    fun getMaxId(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val maxId = repository.getMaxId()
            callback(maxId)
        }
    }

    /**
     * Loads detailed information for a BaliseEntity from a remote API service.
     *
     * @param balise The BaliseEntity for which to fetch additional information.
     * @param onResult Callback function with the fetched Balise details.
     */
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