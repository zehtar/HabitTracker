package com.example.habittracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittracker.ui.components.WeekSelector
import com.example.habittracker.ui.model.StatisticsUiModel
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import java.time.LocalDate
import java.time.temporal.TemporalQueries.localDate

@Composable
fun StatisticsScreen() {

    val context = LocalContext.current

    val viewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(context)
    )
    val weekStart by viewModel.weekStart.collectAsState()

    val selectedDate by viewModel.selectedDate.collectAsState()

    val statistics by viewModel.statistics.collectAsState(
        initial = StatisticsUiModel()
    )

    Column {

        WeekSelector(

            weekStart = weekStart,

            selectedDate = selectedDate,

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

        Text(
            "Статистика"
        )

        Spacer(
            Modifier.height(16.dp)
        )

        Text(
            "Дата: ${statistics.selectedDate}"
        )

        Spacer(
            Modifier.height(24.dp)
        )

        Text(
            "Сегодня"
        )

        Text(
            "Выполнено: ${statistics.completedToday}/${statistics.totalHabits}"
        )

        Text(
            "Процент выполнения: ${statistics.completionPercent}%"
        )

        Text(
            "Минут: ${statistics.totalMinutesToday}"
        )

        Spacer(
            Modifier.height(24.dp)
        )

        Text(
            "Неделя"
        )

        Text(
            "Выполнено: ${statistics.completedWeek}"
        )

        Text(
            "Минут: ${statistics.totalMinutesWeek}"
        )

        Text(
            "Среднее в день: ${statistics.averageMinutesPerDay}"
        )

    }
}


