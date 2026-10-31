package com.example.habittracker.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekly_plan_entries",
    foreignKeys = [
        ForeignKey(
            entity = WeeklyPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["weeklyPlanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("weeklyPlanId"),
        Index(value = ["weeklyPlanId", "habitId", "date"], unique = true)
    ]
)
data class WeeklyPlanEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weeklyPlanId: Int,
    val habitId: Int,
    val date: String,
    val habitType: String,
    val completed: Boolean,
    val minutesSpent: Int,
    val count: Int
)
