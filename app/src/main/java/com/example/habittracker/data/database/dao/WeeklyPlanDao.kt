package com.example.habittracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.data.database.entity.WeeklyPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyPlanDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(plan: WeeklyPlanEntity): Long

    @Update
    suspend fun update(plan: WeeklyPlanEntity)

    @Delete
    suspend fun delete(plan: WeeklyPlanEntity)

    @Query("SELECT * FROM weekly_plans WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): WeeklyPlanEntity?

    @Query("SELECT * FROM weekly_plans WHERE weekStartDate = :weekStartDate LIMIT 1")
    suspend fun getByWeekStart(weekStartDate: String): WeeklyPlanEntity?

    @Query("""
        SELECT * FROM weekly_plans
        WHERE status = 'DRAFT'
          AND weekStartDate <= :date
          AND weekEndDate >= :date
    """)
    suspend fun getDraftPlansForDate(date: String): List<WeeklyPlanEntity>

    @Query("SELECT * FROM weekly_plans ORDER BY weekStartDate DESC")
    fun observeAll(): Flow<List<WeeklyPlanEntity>>

    @Query("SELECT * FROM weekly_plans WHERE status = :status ORDER BY weekStartDate DESC")
    fun observeByStatus(status: String): Flow<List<WeeklyPlanEntity>>
}
