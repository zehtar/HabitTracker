package com.example.habittracker.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_entries",
    indices = [
        Index(
            value = ["habitId", "date"],
            unique = true
        )
    ]
)
data class HabitEntryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val habitId: Int,

    val date: String,

    val completed: Boolean,

    val minutesSpent: Int
)