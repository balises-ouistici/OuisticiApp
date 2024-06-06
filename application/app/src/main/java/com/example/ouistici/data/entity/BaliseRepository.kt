package com.example.ouistici.data.entity

import androidx.lifecycle.LiveData
import com.example.ouistici.model.Balise

class BaliseRepository(private val baliseDao: BaliseDao) {
    val allBalises: LiveData<List<BaliseEntity>> = baliseDao.getAll()

    suspend fun insert(balise: BaliseEntity) {
        baliseDao.insert(balise)
    }

    suspend fun delete(balise: BaliseEntity) {
        baliseDao.delete(balise)
    }

    suspend fun update(balise: BaliseEntity) {
        baliseDao.update(balise)
    }
}