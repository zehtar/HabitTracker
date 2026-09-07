package com.example.habittracker.widget

import androidx.glance.action.ActionParameters

data class HabitWidgetItem(
    val id: Int,
    val name: String,
    val icon: String,
    val completed: Boolean,
    val minutesSpent: Int,
    val color: Long
)

val HabitIdKey =
    ActionParameters.Key<Int>("habit_id")
