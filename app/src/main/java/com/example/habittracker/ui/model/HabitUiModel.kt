
package com.example.habittracker.ui.model

data class HabitUiModel(
    val id: Int,
    val name: String,
    val icon: String,
    val color: Long,
    val completed: Boolean,
    val minutesSpent: Int,
    val habitType: String = "TIME",
    val guideline: String? = null,
    val count: Int = 0,
    val category: String? = null,
    val priority: String = "IMPORTANT",
    val isPaused: Boolean = false
)
