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
        HabitPauseEntity::class,
        WeeklyPlanEntity::class,
        CategoryPlanEntity::class,
        CategoryPlanTargetEntity::class,
        CategoryPlanHabitEntity::class,
        WeeklyPlanEntryEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitEntryDao(): HabitEntryDao
    abstract fun habitReminderDao(): HabitReminderDao

    abstract fun habitPauseDao(): HabitPauseDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao
    abstract fun categoryPlanDao(): CategoryPlanDao
    abstract fun categoryPlanTargetDao(): CategoryPlanTargetDao
    abstract fun categoryPlanHabitDao(): CategoryPlanHabitDao
    abstract fun weeklyPlanEntryDao(): WeeklyPlanEntryDao
}


val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS weekly_plan_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                weeklyPlanId INTEGER NOT NULL,
                habitId INTEGER NOT NULL,
                date TEXT NOT NULL,
                habitType TEXT NOT NULL,
                completed INTEGER NOT NULL,
                minutesSpent INTEGER NOT NULL,
                count INTEGER NOT NULL,
                FOREIGN KEY(weeklyPlanId)
                    REFERENCES weekly_plans(id)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        database.execSQL("""
            CREATE INDEX IF NOT EXISTS
            index_weekly_plan_entries_weeklyPlanId
            ON weekly_plan_entries(weeklyPlanId)
        """.trimIndent())

        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS
            index_weekly_plan_entries_weeklyPlanId_habitId_date
            ON weekly_plan_entries(weeklyPlanId, habitId, date)
        """.trimIndent())
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS weekly_plans (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                weekStartDate TEXT NOT NULL,
                weekEndDate TEXT NOT NULL,
                createdAt TEXT NOT NULL,
                status TEXT NOT NULL,
                note TEXT,
                loadRating TEXT
            )
        """.trimIndent())

        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS
            index_weekly_plans_weekStartDate
            ON weekly_plans(weekStartDate)
        """.trimIndent())

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS category_plans (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                weeklyPlanId INTEGER NOT NULL,
                categoryKey TEXT NOT NULL,
                categoryName TEXT NOT NULL,
                FOREIGN KEY(weeklyPlanId)
                    REFERENCES weekly_plans(id)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        database.execSQL("""
            CREATE INDEX IF NOT EXISTS
            index_category_plans_weeklyPlanId
            ON category_plans(weeklyPlanId)
        """.trimIndent())

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS category_plan_targets (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                categoryPlanId INTEGER NOT NULL,
                targetType TEXT NOT NULL,
                targetValue INTEGER NOT NULL,
                unit TEXT NOT NULL,
                actualValue INTEGER,
                FOREIGN KEY(categoryPlanId)
                    REFERENCES category_plans(id)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        database.execSQL("""
            CREATE INDEX IF NOT EXISTS
            index_category_plan_targets_categoryPlanId
            ON category_plan_targets(categoryPlanId)
        """.trimIndent())

        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS
            index_category_plan_targets_categoryPlanId_targetType
            ON category_plan_targets(categoryPlanId, targetType)
        """.trimIndent())

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS category_plan_habits (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                categoryPlanId INTEGER NOT NULL,
                habitId INTEGER NOT NULL,
                habitName TEXT NOT NULL,
                habitType TEXT NOT NULL,
                FOREIGN KEY(categoryPlanId)
                    REFERENCES category_plans(id)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        database.execSQL("""
            CREATE INDEX IF NOT EXISTS
            index_category_plan_habits_categoryPlanId
            ON category_plan_habits(categoryPlanId)
        """.trimIndent())

        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS
            index_category_plan_habits_categoryPlanId_habitId
            ON category_plan_habits(categoryPlanId, habitId)
        """.trimIndent())
    }
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