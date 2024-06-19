package com.example.ouistici.data.entity

import androidx.lifecycle.LiveData

/**
 * Repository class for managing operations related to `BaliseEntity`.
 *
 * This class provides a layer of abstraction between the data sources (DAO) and the ViewModel.
 *
 * @property baliseDao Data Access Object (DAO) for `BaliseEntity`.
 * @property allBalises LiveData list of all `BaliseEntity` objects.
 */
class BaliseRepository(private val baliseDao: BaliseDao) {
    /**
     * LiveData list of all `BaliseEntity` objects retrieved from the DAO.
     */
    val allBalises: LiveData<List<BaliseEntity>> = baliseDao.getAll()

    /**
     * Inserts a `BaliseEntity` object into the database.
     *
     * @param balise The `BaliseEntity` object to insert.
     */
    suspend fun insert(balise: BaliseEntity) {
        baliseDao.insert(balise)
    }

    /**
     * Deletes a `BaliseEntity` object from the database.
     *
     * @param balise The `BaliseEntity` object to delete.
     */
    suspend fun delete(balise: BaliseEntity) {
        baliseDao.delete(balise)
    }

    /**
     * Updates a `BaliseEntity` object in the database.
     *
     * @param balise The `BaliseEntity` object to update.
     */
    suspend fun update(balise: BaliseEntity) {
        baliseDao.update(balise)
    }

    /**
     * Retrieves the maximum ID from the `BaliseEntity` table.
     *
     * @return The maximum ID as an [Int], or `0` if no entries are present.
     */
    suspend fun getMaxId(): Int {
        return baliseDao.getMaxId() ?: 0
    }
}