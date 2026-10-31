package com.example.habittracker.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_plan_targets",
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
        Index(value = ["categoryPlanId", "targetType"], unique = true)
    ]
)
data class CategoryPlanTargetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryPlanId: Int,
    val targetType: String,
    val targetValue: Int,
    val unit: String,
    val actualValue: Int? = null
)
