package com.example.habittracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habittracker.data.database.entity.CategoryPlanHabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryPlanHabitDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(habits: List<CategoryPlanHabitEntity>)

    @Query("SELECT * FROM category_plan_habits WHERE categoryPlanId = :categoryPlanId ORDER BY habitName")
    suspend fun getForCategory(categoryPlanId: Int): List<CategoryPlanHabitEntity>

    @Query("SELECT * FROM category_plan_habits WHERE categoryPlanId = :categoryPlanId ORDER BY habitName")
    fun observeForCategory(categoryPlanId: Int): Flow<List<CategoryPlanHabitEntity>>
}
