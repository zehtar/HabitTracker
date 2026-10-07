package com.example.habittracker.ui.model

import java.time.LocalDate

enum class FullStatisticsPeriod {
    ALL_TIME,
    DAY,
    WEEK,
    MONTH,
    YEAR
}

data class FullStatisticsUiModel(
    val period: FullStatisticsPeriod = FullStatisticsPeriod.ALL_TIME,
    val overall: FullStatisticsOverallUiModel = FullStatisticsOverallUiModel(),
    val activity: List<FullStatisticsDayUiModel> = emptyList(),
    val monthlyComparison: MonthlyComparisonUiModel = MonthlyComparisonUiModel(),
    val selectedHabit: HabitFullStatisticsUiModel? = null,
    val habitCalendar: List<HabitCalendarDayUiModel> = emptyList()
)

data class FullStatisticsOverallUiModel(
    val completed: Int = 0,
    val totalMinutes: Int = 0,
    val completionPercent: Int = 0,
    val averageMinutesPerCompletion: Int = 0,
    val averageMinutesPerDay: Int = 0
)

data class FullStatisticsDayUiModel(
    val date: LocalDate,
    val completed: Int,
    val total: Int,
    val completionPercent: Int,
    val minutes: Int
)

data class MonthlyComparisonUiModel(
    val currentMonth: LocalDate = LocalDate.now().withDayOfMonth(1),
    val previousMonth: LocalDate = LocalDate.now()
        .withDayOfMonth(1)
        .minusMonths(1),

    val currentCompleted: Int = 0,
    val previousCompleted: Int = 0,

    val currentMinutes: Int = 0,
    val previousMinutes: Int = 0,

    val completedChangePercent: Int = 0,
    val minutesChangePercent: Int = 0
)

data class HabitFullStatisticsUiModel(
    val habitId: Int,
    val name: String,
    val icon: String,
    val color: Long,

    val currentStreak: Int = 0,
    val bestStreak: Int = 0,

    val completed: Int = 0,
    val completionPercent: Int = 0,

    val totalMinutes: Int = 0,
    val averageMinutesPerCompletion: Int = 0,
    val averageMinutesPerDay: Int = 0
)

data class HabitCalendarDayUiModel(
    val date: LocalDate,
    val completed: Boolean,
    val minutes: Int
)