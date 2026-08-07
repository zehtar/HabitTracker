package com.example.habittracker.widget

data class HabitWidgetItem(
    val id: Int,
    val name: String,
    val icon: String,
    val completed: Boolean,
    val minutesSpent: Int,
    val color: Long
)
