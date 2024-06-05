package com.example.ouistici.data.entity

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [BaliseEntity::class], version = 1, exportSchema = false)
@TypeConverters(AnnonceListConverter::class, PlageHoraireListConverter::class)
abstract class BaliseDatabase : RoomDatabase() {
    abstract fun baliseDao(): BaliseDao

    companion object {
        @Volatile
        private var INSTANCE: BaliseDatabase? = null

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

