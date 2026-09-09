package com.example.habittracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.habittracker.ui.components.WeekSelector
import com.example.habittracker.ui.components.WeeklyActivityChart
import com.example.habittracker.ui.components.WeeklyTimeChart
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import com.example.habittracker.ui.components.*

@Composable
fun ActivityStatisticsScreen() {

    val context = LocalContext.current

    val viewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(context)
    )

    val weekStart by viewModel.weekStart.collectAsState()

    val statistics by viewModel.statistics.collectAsState(
        initial = com.example.habittracker.ui.model.StatisticsUiModel()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(16.dp)
    ) {

        WeekSelector(
            weekStart = weekStart,

            selectedDate = statistics.selectedDate,

            onPreviousWeek = {
                viewModel.previousWeek()
            },

            onNextWeek = {
                viewModel.nextWeek()
            },

            onDateSelected = {
                viewModel.selectDate(it)
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Активность",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        WeeklyActivityChart(
            statistics = statistics.dailyStatistics
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "За неделю",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        WeeklySummaryCard(
            completed = statistics.completedWeek,
            minutes = statistics.totalMinutesWeek,
            average = statistics.averageMinutesPerDay
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Время по дням",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        WeeklyTimeChart(
            statistics = statistics.dailyStatistics
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "По дням",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        statistics.dailyStatistics.forEach { day ->

            DailyStatisticsCard(
                statistic = day
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}