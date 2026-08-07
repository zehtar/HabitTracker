package com.example.habittracker.data.database.dao
import androidx.room.*
import com.example.habittracker.data.database.entity.HabitEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitEntryDao {

    @Upsert
    suspend fun upsertEntry(entry: HabitEntryEntity)

    @Query("SELECT * FROM habit_entries")
    fun getAllEntries(): Flow<List<HabitEntryEntity>>

    @Query(
        """
        SELECT * FROM habit_entries
        WHERE habitId = :habitId
        """
    )
    suspend fun getEntriesForHabit(
        habitId: Int
    ): List<HabitEntryEntity>

    @Query(
        """
        SELECT * FROM habit_entries
        WHERE habitId = :habitId
        AND date = :date
        LIMIT 1
        """
    )
    suspend fun getEntryForDay(
        habitId: Int,
        date: String
    ): HabitEntryEntity?

    @Query(
        """
    SELECT * FROM habit_entries
    WHERE date = :date
    """
    )
    fun getEntriesForDate(
        date: String
    ): Flow<List<HabitEntryEntity>>

    @Query(
        """
    SELECT * FROM habit_entries
    WHERE date = :date
    """
    )
    suspend fun getEntriesForDateOnce(
        date: String
    ): List<HabitEntryEntity>
}