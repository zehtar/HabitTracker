package com.example.habittracker.ui.model

data class HabitUiModel(
    val id: Int,
    val name: String,
    val icon: String,
    val color: Long,

    val completed: Boolean,
    val minutesSpent: Int
)