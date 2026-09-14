package com.example.habittracker.ui.model

import java.time.LocalDate

data class StatisticsUiModel(

    val selectedDate: LocalDate = LocalDate.now(),
    val completedToday: Int = 0,
    val totalHabits: Int = 0,
    val completedWeek: Int = 0,
    val totalMinutesToday: Int = 0,
    val totalMinutesWeek: Int = 0,
    val completionPercent: Int = 0,
    val averageMinutesPerDay: Int = 0,
    val dailyStatistics: List<DailyStatisticUiModel> = emptyList()
)

data class DailyStatisticUiModel(
    val date: LocalDate,
    val completed: Int,
    val total: Int,
    val completionPercent: Int,
    val minutes: Int
)

data class CalendarDayUiModel(
    val date: LocalDate,
    val completed: Int,
    val total: Int,
    val completionPercent: Int
)

data class CalendarDayHabitUiModel(
    val habitId: Int,
    val name: String,
    val icon: String,
    val color: Long,
    val minutesSpent: Int
)

data class HabitTimeStatisticUiModel(
    val habitId: Int,
    val name: String,
    val icon: String,
    val color: Long,
    val minutes: Int
)