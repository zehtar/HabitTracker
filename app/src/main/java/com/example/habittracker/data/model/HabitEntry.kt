package com.example.habittracker.data.model

import java.time.LocalDate

data class HabitEntry(
    val habitId: Int,
    val date: LocalDate,
    val completed: Boolean,
    val minutesSpent: Int
)