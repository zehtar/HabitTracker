package com.example.habittracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.habittracker.data.database.dao.*
import com.example.habittracker.data.database.entity.*

@Database(
    entities = [
        HabitEntity::class,
        HabitEntryEntity::class,
        HabitReminderEntity::class,
        HabitPauseEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitEntryDao(): HabitEntryDao
    abstract fun habitReminderDao(): HabitReminderDao

    abstract fun habitPauseDao(): HabitPauseDao
}
val MIGRATION_6_7 = object : Migration(6, 7) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
        ALTER TABLE habits
        ADD COLUMN streakResetDate TEXT DEFAULT NULL
    """)

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS habit_pauses (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                habitId INTEGER NOT NULL,
                startDate TEXT NOT NULL,
                endDate TEXT
            )
        """)

        database.execSQL("""
            CREATE INDEX IF NOT EXISTS
            index_habit_pauses_habitId_startDate
            ON habit_pauses(habitId, startDate)
        """)
    }
}
val MIGRATION_5_6 = object : Migration(5, 6) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            ALTER TABLE habits
            ADD COLUMN category TEXT
            """.trimIndent()
        )

        database.execSQL(
            """
            ALTER TABLE habits
            ADD COLUMN priority TEXT NOT NULL DEFAULT 'IMPORTANT'
            """.trimIndent()
        )
    }
}


val MIGRATION_4_5 = object : Migration(4, 5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            ALTER TABLE habits
            ADD COLUMN habitType TEXT NOT NULL DEFAULT 'TIME'
            """.trimIndent()
        )

        database.execSQL(
            """
            ALTER TABLE habits
            ADD COLUMN guideline TEXT
            """.trimIndent()
        )

        database.execSQL(
            """
            ALTER TABLE habit_entries
            ADD COLUMN count INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )
    }
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

val MIGRATION_3_4 = object : Migration(3, 4) {

    override fun migrate(
        database: SupportSQLiteDatabase
    ) {
        database.execSQL(
            """
            ALTER TABLE habit_reminders
            ADD COLUMN requireCompletion INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )
    }
}