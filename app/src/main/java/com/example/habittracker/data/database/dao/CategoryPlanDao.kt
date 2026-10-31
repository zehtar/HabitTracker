package com.example.habittracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habittracker.data.database.entity.CategoryPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryPlanDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(categoryPlan: CategoryPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(categoryPlans: List<CategoryPlanEntity>)

    @Delete
    suspend fun delete(categoryPlan: CategoryPlanEntity)

    @Query("SELECT * FROM category_plans WHERE weeklyPlanId = :weeklyPlanId ORDER BY id")
    suspend fun getForWeek(weeklyPlanId: Int): List<CategoryPlanEntity>

    @Query("SELECT * FROM category_plans WHERE weeklyPlanId = :weeklyPlanId ORDER BY id")
    fun observeForWeek(weeklyPlanId: Int): Flow<List<CategoryPlanEntity>>
}
