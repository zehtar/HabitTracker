package com.example.habittracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.habittracker.data.database.dao.HabitDao
import com.example.habittracker.data.database.dao.HabitEntryDao
import com.example.habittracker.data.database.entity.HabitEntity
import com.example.habittracker.data.database.entity.HabitEntryEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitEntryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    abstract fun habitEntryDao(): HabitEntryDao
}