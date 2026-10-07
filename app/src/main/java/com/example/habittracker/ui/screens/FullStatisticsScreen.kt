package com.example.habittracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittracker.data.database.entity.HabitEntity
import com.example.habittracker.ui.model.FullStatisticsPeriod
import com.example.habittracker.ui.model.HabitFullStatisticsUiModel
import com.example.habittracker.ui.model.MonthlyComparisonUiModel
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import java.time.LocalDate
import java.time.Month

@Composable
fun FullStatisticsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(context)
    )

    val selectedPeriod by viewModel
        .selectedFullStatisticsPeriod
        .collectAsState()

    val selectedHabitId by viewModel
        .selectedFullStatisticsHabit
        .collectAsState()

    val fullStatistics by viewModel
        .fullStatistics
        .collectAsState()

    val habits by viewModel
        .allHabits
        .collectAsState(emptyList())

    val overall = fullStatistics.overall

    LaunchedEffect(habits) {
        if (
            selectedHabitId == null &&
            habits.isNotEmpty()
        ) {
            viewModel.selectFullStatisticsHabit(
                habits.first().id
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = "Полная статистика",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ---------------------------------------------------------
        // PERIOD
        // ---------------------------------------------------------

        SectionTitle(
            text = "Период"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        PeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = {
                viewModel.selectFullStatisticsPeriod(it)
            }
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ---------------------------------------------------------
        // GENERAL ACTIVITY
        // ---------------------------------------------------------

        SectionTitle(
            text = "Общая активность"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        FullStatisticsCard {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                StatisticsValue(
                    value = overall.completed.toString(),
                    label = "Выполнено",
                    modifier = Modifier.weight(1f)
                )

                StatisticsValue(
                    value = formatMinutes(
                        overall.totalMinutes
                    ),
                    label = "Время",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                StatisticsValue(
                    value = "${overall.completionPercent}%",
                    label = "Успешность",
                    modifier = Modifier.weight(1f)
                )

                StatisticsValue(
                    value = "${overall.averageMinutesPerCompletion} мин",
                    label = "Среднее",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ---------------------------------------------------------
        // DYNAMICS
        // ---------------------------------------------------------

        SectionTitle(
            text = "Динамика"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ActivityPlaceholderCard()

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ---------------------------------------------------------
        // MONTH COMPARISON
        // ---------------------------------------------------------

        SectionTitle(
            text = "Сравнение месяцев"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        MonthlyComparisonCard(
            comparison = fullStatistics.monthlyComparison
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ---------------------------------------------------------
        // HABIT ANALYSIS
        // ---------------------------------------------------------

        SectionTitle(
            text = "Анализ привычки"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        HabitSelector(
            habits = habits,
            selectedHabitId = selectedHabitId,
            onHabitSelected = { habitId ->
                viewModel.selectFullStatisticsHabit(habitId)
            }
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        fullStatistics.selectedHabit?.let { habit ->

            HabitStatisticsCard(
                statistics = habit
            )
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ---------------------------------------------------------
        // CALENDAR
        // ---------------------------------------------------------

        SectionTitle(
            text = "История выполнения"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        CalendarPlaceholderCard()

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

// =============================================================
// COMMON CARD
// =============================================================

@Composable
fun FullStatisticsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(
                alpha = 0.30f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// =============================================================
// STATISTICS VALUE
// =============================================================

@Composable
fun StatisticsValue(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(
                alpha = 0.22f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 12.dp
            )
        ) {

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =============================================================
// SECTION TITLE
// =============================================================

@Composable
fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

// =============================================================
// PERIOD SELECTOR
// =============================================================

@Composable
fun PeriodSelector(
    selectedPeriod: FullStatisticsPeriod,
    onPeriodSelected: (FullStatisticsPeriod) -> Unit
) {
    val periods = listOf(
        FullStatisticsPeriod.ALL_TIME to "Всё время",
        FullStatisticsPeriod.DAY to "День",
        FullStatisticsPeriod.WEEK to "Неделя",
        FullStatisticsPeriod.MONTH to "Месяц",
        FullStatisticsPeriod.YEAR to "Год"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.take(3).forEach { (period, title) ->

                if (period == selectedPeriod) {
                    Button(
                        onClick = {
                            onPeriodSelected(period)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(title)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            onPeriodSelected(period)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(title)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.drop(3).forEach { (period, title) ->

                if (period == selectedPeriod) {
                    Button(
                        onClick = {
                            onPeriodSelected(period)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(title)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            onPeriodSelected(period)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(title)
                    }
                }
            }
        }
    }
}

// =============================================================
// ACTIVITY PLACEHOLDER
// =============================================================

@Composable
fun ActivityPlaceholderCard() {

    FullStatisticsCard {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "График динамики",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Здесь будет график активности",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =============================================================
// MONTHLY COMPARISON
// =============================================================

@Composable
fun MonthlyComparisonCard(
    comparison: MonthlyComparisonUiModel
) {
    FullStatisticsCard {

        Text(
            text = "${formatMonth(comparison.previousMonth)} → " +
                    formatMonth(comparison.currentMonth),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        ComparisonRow(
            title = "Выполнено",
            oldValue = comparison.previousCompleted.toString(),
            newValue = comparison.currentCompleted.toString(),
            change = formatChangePercent(
                comparison.completedChangePercent
            )
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ComparisonRow(
            title = "Время",
            oldValue = formatMinutes(
                comparison.previousMinutes
            ),
            newValue = formatMinutes(
                comparison.currentMinutes
            ),
            change = formatChangePercent(
                comparison.minutesChangePercent
            )
        )
    }
}

fun formatChangePercent(
    change: Int
): String {
    return when {
        change > 0 -> "+$change%"
        change < 0 -> "$change%"
        else -> "0%"
    }
}
fun formatMonth(
    date: LocalDate
): String {
    return when (date.month) {
        Month.JANUARY -> "Январь"
        Month.FEBRUARY -> "Февраль"
        Month.MARCH -> "Март"
        Month.APRIL -> "Апрель"
        Month.MAY -> "Май"
        Month.JUNE -> "Июнь"
        Month.JULY -> "Июль"
        Month.AUGUST -> "Август"
        Month.SEPTEMBER -> "Сентябрь"
        Month.OCTOBER -> "Октябрь"
        Month.NOVEMBER -> "Ноябрь"
        Month.DECEMBER -> "Декабрь"
    }
}

// =============================================================
// COMPARISON ROW
// =============================================================

@Composable
fun ComparisonRow(
    title: String,
    oldValue: String,
    newValue: String,
    change: String
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(
                alpha = 0.20f
            )
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = oldValue,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = " → ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = newValue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = change,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// =============================================================
// HABIT SELECTOR
// =============================================================

@Composable
fun HabitSelector(
    habits: List<HabitEntity>,
    selectedHabitId: Int?,
    onHabitSelected: (Int) -> Unit
) {
    FullStatisticsCard {

        Text(
            text = "Привычка",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (habits.isEmpty()) {
            Text(
                text = "Нет привычек",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = habits,
                    key = { it.id }
                ) { habit ->

                    if (habit.id == selectedHabitId) {
                        Button(
                            onClick = {
                                onHabitSelected(habit.id)
                            }
                        ) {
                            Text(habit.name)
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                onHabitSelected(habit.id)
                            }
                        ) {
                            Text(habit.name)
                        }
                    }
                }
            }
        }
    }
}
// =============================================================
// HABIT STATISTICS
// =============================================================

@Composable
fun HabitStatisticsCard(
    statistics: HabitFullStatisticsUiModel
) {
    FullStatisticsCard {

        Text(
            text = statistics.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatisticsValue(
                value = "${statistics.currentStreak} дней",
                label = "Текущая серия",
                modifier = Modifier.weight(1f)
            )

            StatisticsValue(
                value = "${statistics.bestStreak} дней",
                label = "Лучшая серия",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatisticsValue(
                value = "${statistics.completionPercent}%",
                label = "Выполнение",
                modifier = Modifier.weight(1f)
            )

            StatisticsValue(
                value = "${statistics.averageMinutesPerCompletion} мин",
                label = "Среднее время",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatisticsValue(
                value = formatMinutes(
                    statistics.totalMinutes
                ),
                label = "Общее время",
                modifier = Modifier.weight(1f)
            )

            StatisticsValue(
                value = "${statistics.averageMinutesPerDay} мин",
                label = "В среднем в день",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return when {
        hours > 0 && remainingMinutes > 0 ->
            "$hours ч $remainingMinutes мин"

        hours > 0 ->
            "$hours ч"

        else ->
            "$minutes мин"
    }
}


// =============================================================
// CALENDAR PLACEHOLDER
// =============================================================

@Composable
fun CalendarPlaceholderCard() {

    FullStatisticsCard {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Календарь выполнения",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Здесь будет история выполнения привычки",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}