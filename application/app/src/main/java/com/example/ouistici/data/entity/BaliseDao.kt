package com.example.ouistici.data.entity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


/**
 * Data Access Object (DAO) for managing `BaliseEntity` operations in the database.
 */
@Dao
interface BaliseDao {
    /**
     * Retrieves all beacons from the database.
     *
     * @return A [LiveData] list of all beacons ([BaliseEntity]).
     */
    @Query("SELECT * FROM balises")
    fun getAll(): LiveData<List<BaliseEntity>>

    /**
     * Inserts a beacon into the database. Replaces any existing entry with the same ID.
     *
     * @param balise The beacon entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(balise: BaliseEntity)

    /**
     * Deletes a beacon from the database.
     *
     * @param balise The beacon entity to delete.
     */
    @Delete
    suspend fun delete(balise: BaliseEntity)

    /**
     * Updates a beacon in the database.
     *
     * @param balise The beacon entity to update.
     */
    @Update
    suspend fun update(balise: BaliseEntity)

    /**
     * Retrieves the maximum ID currently present in the beacon table.
     *
     * @return The highest beacon ID as an [Int], or `null` if the table is empty.
     */
    @Query("SELECT MAX(id) FROM balises")
    suspend fun getMaxId(): Int?
}