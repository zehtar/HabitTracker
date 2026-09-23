package com.example.habittracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.habittracker.data.database.dao.HabitDao
import com.example.habittracker.data.database.dao.HabitEntryDao
import com.example.habittracker.data.database.dao.HabitReminderDao
import com.example.habittracker.data.database.entity.HabitEntity
import com.example.habittracker.data.database.entity.HabitEntryEntity
import com.example.habittracker.data.database.entity.HabitReminderEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitEntryEntity::class,
        HabitReminderEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    abstract fun habitEntryDao(): HabitEntryDao

    abstract fun habitReminderDao(): HabitReminderDao
}

val MIGRATION_2_3 = object : Migration(2, 3) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS habit_reminders (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                habitId INTEGER NOT NULL,
                hour INTEGER NOT NULL,
                minute INTEGER NOT NULL,
                enabled INTEGER NOT NULL,
                repeatType TEXT NOT NULL,
                FOREIGN KEY(habitId)
                    REFERENCES habits(id)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_habit_reminders_habitId
            ON habit_reminders(habitId)
            """.trimIndent()
        )
    }
}