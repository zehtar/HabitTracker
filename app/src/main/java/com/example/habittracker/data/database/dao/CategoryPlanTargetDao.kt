package com.example.habittracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.data.database.entity.CategoryPlanTargetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryPlanTargetDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(target: CategoryPlanTargetEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(targets: List<CategoryPlanTargetEntity>)

    @Update
    suspend fun update(target: CategoryPlanTargetEntity)

    @Query("SELECT * FROM category_plan_targets WHERE categoryPlanId = :categoryPlanId ORDER BY targetType")
    suspend fun getForCategory(categoryPlanId: Int): List<CategoryPlanTargetEntity>

    @Query("SELECT * FROM category_plan_targets WHERE categoryPlanId = :categoryPlanId ORDER BY targetType")
    fun observeForCategory(categoryPlanId: Int): Flow<List<CategoryPlanTargetEntity>>
}
