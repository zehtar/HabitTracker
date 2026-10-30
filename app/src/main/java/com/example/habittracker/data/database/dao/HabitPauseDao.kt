
package com.example.habittracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.habittracker.data.database.entity.HabitPauseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitPauseDao {

    @Insert
    suspend fun insertPause(pause: HabitPauseEntity)

    @Query("""
        SELECT * FROM habit_pauses
        WHERE habitId = :habitId
        ORDER BY startDate
    """)
    suspend fun getPausesForHabit(
        habitId: Int
    ): List<HabitPauseEntity>

    @Query("""
        SELECT * FROM habit_pauses
        WHERE habitId = :habitId
        ORDER BY startDate
    """)
    fun observePausesForHabit(
        habitId: Int
    ): Flow<List<HabitPauseEntity>>

    @Query("""
        SELECT * FROM habit_pauses
        WHERE habitId = :habitId
        AND endDate IS NULL
        LIMIT 1
    """)
    suspend fun getOpenPause(
        habitId: Int
    ): HabitPauseEntity?

    @Query("""
        UPDATE habit_pauses
        SET endDate = :endDate
        WHERE id = :pauseId
    """)
    suspend fun finishPause(
        pauseId: Int,
        endDate: String
    )

    @Query("""
        DELETE FROM habit_pauses
        WHERE id = :pauseId
    """)
    suspend fun deletePause(
        pauseId: Int
    )

    @Query("DELETE FROM habit_pauses")
    suspend fun deleteAllPauses()

    @Query("SELECT * FROM habit_pauses")
    suspend fun getAllPausesOnce(): List<HabitPauseEntity>

    @Insert
    suspend fun insertPauses(
        pauses: List<HabitPauseEntity>
    )
    @Query("""
    SELECT * FROM habit_pauses
    WHERE habitId = :habitId
    AND startDate <= :date
    AND (endDate IS NULL OR endDate >= :date)
    LIMIT 1
""")
    suspend fun getActivePause(
        habitId: Int,
        date: String
    ): HabitPauseEntity?

    @Query("""
    DELETE FROM habit_pauses
    WHERE habitId = :habitId
""")
    suspend fun deletePausesForHabit(habitId: Int)

    @Query("SELECT * FROM habit_pauses")
    fun getAllPauses(): Flow<List<HabitPauseEntity>>
}
