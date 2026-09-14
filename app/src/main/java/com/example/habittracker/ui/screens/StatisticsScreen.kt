package com.example.habittracker.ui.screens

import androidx.compose.foundation.layout.*
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
import com.example.habittracker.ui.components.*
import com.example.habittracker.ui.model.StatisticsUiModel
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries.localDate
import java.util.Locale
import com.example.habittracker.ui.components.WeeklyActivityChart
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton

@Composable
fun StatisticsScreen(
    onOpenActivityStatistics: () -> Unit
) {
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
            .verticalScroll(
                rememberScrollState()
            )
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

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Активность",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = onOpenActivityStatistics
            ) {
                Text("Подробнее")
            }
        }
        WeeklyActivityChart(
            statistics = statistics.dailyStatistics
        )
        /// КАЛЕНДАРЬ
        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Календарь",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        val calendarMonth by viewModel
            .calendarMonth
            .collectAsState()

        val calendarStatistics by viewModel
            .calendarStatistics
            .collectAsState(initial = emptyList())

        MonthlyCalendar(
            month = calendarMonth,
            days = calendarStatistics,
            onPreviousMonth = {
                viewModel.previousCalendarMonth()
            },
            onNextMonth = {
                viewModel.nextCalendarMonth()
            },
            onDayClick = {
                viewModel.selectCalendarDay(it)
            }
        )
        val calendarSelectedDay by viewModel
            .calendarSelectedDay
            .collectAsState()

        val calendarDayDetails by viewModel
            .calendarDayDetails
            .collectAsState(initial = emptyList())

        calendarSelectedDay?.let { day ->

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            CalendarDayDetailsCard(
                day = day,
                habits = calendarDayDetails
            )
        }
        // НЕДЕЛЯ
        val habitTimeForWeek by viewModel
            .habitTimeForWeek
            .collectAsState(initial = emptyList())

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Время по привычкам за неделю",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        HabitTimeSummaryCard(
            statistics = habitTimeForWeek
        )
        // МЕСЯЦ
        val habitTimeForMonth by viewModel
            .habitTimeForMonth
            .collectAsState(initial = emptyList())

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Время по привычкам за месяц",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        HabitTimeSummaryCard(
            statistics = habitTimeForMonth
        )
    }
}



