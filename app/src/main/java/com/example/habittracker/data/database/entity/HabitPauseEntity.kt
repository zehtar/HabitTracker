
package com.example.habittracker.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_pauses",
    indices = [
        Index(value = ["habitId", "startDate"])
    ]
)
data class HabitPauseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val habitId: Int,

    // Первый день паузы, включительно.
    val startDate: String,

    // Последний день паузы, включительно.
    // null означает бессрочную паузу.
    val endDate: String? = null
)
