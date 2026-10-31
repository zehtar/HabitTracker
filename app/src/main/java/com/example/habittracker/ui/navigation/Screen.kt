package com.example.habittracker.ui.navigation

sealed class Screen(
    val route: String
) {

    data object Habits : Screen("habits")

    data object WeeklyPlan : Screen("weekly_plan")

    data object Statistics : Screen("statistics")

    data object Settings : Screen("settings")

    data object ActivityStatistics : Screen("activity_statistics")

    data object FullStatistics : Screen("full_statistics")

    data object Reminders : Screen("reminders")
}
