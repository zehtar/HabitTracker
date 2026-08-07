package com.example.habittracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittracker.ui.components.StatisticsCard
import com.example.habittracker.ui.components.TodayProgressCard
import com.example.habittracker.ui.components.WeekSelector
import com.example.habittracker.ui.components.WeeklyStatisticsCard
import com.example.habittracker.ui.model.StatisticsUiModel
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries.localDate
import java.util.Locale

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

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

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Статистика",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = statistics.selectedDate
                .format(
                    DateTimeFormatter.ofPattern(
                        "EEEE, d MMMM",
                        Locale("ru")
                    )
                )
                .replaceFirstChar { it.uppercase() },

            style = MaterialTheme.typography.bodyLarge,

            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            StatisticsCard(
                title = "Выполнено",
                value = "${statistics.completionPercent}%",
                description = "сегодня",
                modifier = Modifier.weight(1f)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            StatisticsCard(
                title = "Время",
                value = "${statistics.totalMinutesToday} мин",
                description = "сегодня",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Сегодня",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TodayProgressCard(
            completed = statistics.completedToday,
            total = statistics.totalHabits,
            percent = statistics.completionPercent
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
            modifier = Modifier.height(8.dp)
        )

        WeeklyStatisticsCard(
            completed = statistics.completedWeek,
            minutes = statistics.totalMinutesWeek,
            average = statistics.averageMinutesPerDay
        )
    }
}



