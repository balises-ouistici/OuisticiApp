package com.example.ouistici.data.entity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ouistici.model.Balise

@Dao
interface BaliseDao {
    @Query("SELECT * FROM balises")
    fun getAll(): LiveData<List<BaliseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(balise: BaliseEntity)

    @Delete
    suspend fun delete(balise: BaliseEntity)

    @Update
    suspend fun update(balise: BaliseEntity)
}