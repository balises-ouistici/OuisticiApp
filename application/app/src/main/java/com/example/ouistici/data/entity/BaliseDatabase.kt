package com.example.ouistici.data.entity

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


/**
 * Abstract Room database class for managing the beacon database.
 *
 * This database holds the [BaliseEntity] and provides access to the [BaliseDao].
 */
@Database(entities = [BaliseEntity::class], version = 1, exportSchema = false)
@TypeConverters(AnnonceListConverter::class, PlageHoraireListConverter::class)
abstract class BaliseDatabase : RoomDatabase() {
    /**
     * Provides access to the DAO for beacon operations.
     *
     * @return The [BaliseDao] instance.
     */
    abstract fun baliseDao(): BaliseDao

    companion object {
        /**
         * The singleton instance of the database.
         */
        @Volatile
        private var INSTANCE: BaliseDatabase? = null

        /**
         * Retrieves the singleton instance of the database.
         *
         * If the instance does not exist, it will be created.
         *
         * @param context The application context.
         * @return The [BaliseDatabase] instance.
         */
        fun getDatabase(context: Context): BaliseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BaliseDatabase::class.java,
                    "balise_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

