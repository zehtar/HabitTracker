package com.example.habittracker.data.database.dao
import androidx.room.*
import com.example.habittracker.data.database.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Upsert
    suspend fun upsertHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Int)

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits")
    suspend fun getAllHabitsOnce(): List<HabitEntity>

    @Insert
    suspend fun insertHabits(
        habits: List<HabitEntity>
    )

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
}