package com.example.habittracker.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekly_plans",
    indices = [Index(value = ["weekStartDate"], unique = true)]
)
data class WeeklyPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weekStartDate: String,
    val weekEndDate: String,
    val createdAt: String,
    val status: String = "DRAFT",
    val note: String? = null,
    val loadRating: String? = null
)
