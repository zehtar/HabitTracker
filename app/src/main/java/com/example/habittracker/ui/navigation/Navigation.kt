package com.example.habittracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habittracker.ui.components.BottomNavigationBar
import com.example.habittracker.ui.screens.HabitTrackerScreen
import com.example.habittracker.ui.screens.StatisticsScreen

@Composable
fun HabitNavigation(modifier: Modifier) {

    val navController = rememberNavController()

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

                HabitTrackerScreen()

            }

            composable(Screen.Statistics.route) {

                StatisticsScreen()

            }

        }

    }

}