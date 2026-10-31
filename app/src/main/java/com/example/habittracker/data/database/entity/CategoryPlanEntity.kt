package com.example.habittracker.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_plans",
    foreignKeys = [
        ForeignKey(
            entity = WeeklyPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["weeklyPlanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("weeklyPlanId")]
)
data class CategoryPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weeklyPlanId: Int,
    val categoryKey: String,
    val categoryName: String
)
