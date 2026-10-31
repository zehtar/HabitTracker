package com.example.habittracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habittracker.data.database.entity.WeeklyPlanEntryEntity

@Dao
interface WeeklyPlanEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntries(entries: List<WeeklyPlanEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: WeeklyPlanEntryEntity)

    @Query(
        """
        SELECT * FROM weekly_plan_entries
        WHERE weeklyPlanId = :weeklyPlanId
        ORDER BY date, habitId
        """
    )
    suspend fun getForPlan(weeklyPlanId: Int): List<WeeklyPlanEntryEntity>

    @Query(
        """
        SELECT * FROM weekly_plan_entries
        WHERE weeklyPlanId = :weeklyPlanId
          AND habitId = :habitId
          AND date = :date
        LIMIT 1
        """
    )
    suspend fun getForHabitDate(
        weeklyPlanId: Int,
        habitId: Int,
        date: String
    ): WeeklyPlanEntryEntity?
}
