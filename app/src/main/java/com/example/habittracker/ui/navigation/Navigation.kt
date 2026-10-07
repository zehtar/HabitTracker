package com.example.habittracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habittracker.ui.components.BottomNavigationBar
import com.example.habittracker.ui.screens.ActivityStatisticsScreen
import com.example.habittracker.ui.screens.HabitTrackerScreen
import com.example.habittracker.ui.screens.RemindersScreen
import com.example.habittracker.ui.screens.StatisticsScreen
import com.example.habittracker.ui.screens.SettingsScreen
import com.example.habittracker.ui.screens.FullStatisticsScreen
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
@Composable
fun HabitNavigation(
    modifier: Modifier,
    initialHabitId: Int = -1
) {
    val navController = rememberNavController()

    val context = LocalContext.current

    val viewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(context)
    )
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController
            )
        }

    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Habits.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Screen.Habits.route) {
                HabitTrackerScreen(
                    initialHabitId = initialHabitId
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen(
                    onOpenActivityStatistics = {
                        navController.navigate(
                            Screen.ActivityStatistics.route
                        )
                    },
                    onOpenFullStatistics = {
                        navController.navigate(
                            Screen.FullStatistics.route
                        )
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(Screen.ActivityStatistics.route) {
                ActivityStatisticsScreen()
            }

            composable(Screen.FullStatistics.route) {
                FullStatisticsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Reminders.route) {
                RemindersScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}