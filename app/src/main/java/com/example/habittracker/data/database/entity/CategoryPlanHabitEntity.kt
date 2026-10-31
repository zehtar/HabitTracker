package com.example.habittracker.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_plan_habits",
    foreignKeys = [
        ForeignKey(
            entity = CategoryPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryPlanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("categoryPlanId"),
        Index(value = ["categoryPlanId", "habitId"], unique = true)
    ]
)
data class CategoryPlanHabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryPlanId: Int,
    val habitId: Int,
    val habitName: String,
    val habitType: String
)
