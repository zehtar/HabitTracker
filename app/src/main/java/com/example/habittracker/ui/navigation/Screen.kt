package com.example.habittracker.ui.navigation

sealed class Screen(
    val route: String
) {

    data object Habits : Screen("habits")

    data object Statistics : Screen("statistics")

}