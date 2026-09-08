package com.example.habittracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.habittracker.ui.navigation.Screen
import androidx.compose.material.icons.filled.Settings
@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val backStackEntry =
        navController.currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.Habits.route,
            onClick = {

                navController.navigate(Screen.Habits.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },

            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Привычки"
                )
            },

            label = {
                Text("Главная")
            }
        )

        NavigationBarItem(

            selected = currentRoute == Screen.Statistics.route,
            onClick = {

                navController.navigate(Screen.Statistics.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },

            icon = {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = "Статистика"
                )
            },

            label = {
                Text("Статистика")
            }
        )
        NavigationBarItem(

            selected = currentRoute == Screen.Settings.route,
            onClick = {

                navController.navigate(Screen.Settings.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },

            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Настройки"
                )
            },

            label = {
                Text("Настройки")
            }
        )
    }
}