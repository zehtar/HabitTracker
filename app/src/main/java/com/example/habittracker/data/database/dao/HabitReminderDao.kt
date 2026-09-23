package com.example.habittracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.data.database.entity.HabitReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitReminderDao {

    @Query(
        """
        SELECT * FROM habit_reminders
        WHERE habitId = :habitId
        ORDER BY hour, minute
        """
    )
    fun getRemindersForHabit(
        habitId: Int
    ): Flow<List<HabitReminderEntity>>

    @Query(
        """
        SELECT * FROM habit_reminders
        WHERE enabled = 1
        """
    )
    fun getEnabledReminders(): Flow<List<HabitReminderEntity>>

    @Query(
        """
        SELECT * FROM habit_reminders
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getReminderById(
        id: Int
    ): HabitReminderEntity?

    @Insert
    suspend fun insertReminder(
        reminder: HabitReminderEntity
    ): Long

    @Update
    suspend fun updateReminder(
        reminder: HabitReminderEntity
    )

    @Delete
    suspend fun deleteReminder(
        reminder: HabitReminderEntity
    )

    @Query(
        """
        DELETE FROM habit_reminders
        WHERE habitId = :habitId
        """
    )
    suspend fun deleteRemindersForHabit(
        habitId: Int
    )
    @Query(
        """
    SELECT * FROM habit_reminders
    ORDER BY hour, minute
    """
    )
    fun getAllReminders(): Flow<List<HabitReminderEntity>>
}