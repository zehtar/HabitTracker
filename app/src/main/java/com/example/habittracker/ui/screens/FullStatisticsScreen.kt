package com.example.habittracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FullStatisticsScreen(
    onBack: () -> Unit
) {

    var selectedPeriod by remember {
        mutableStateOf("Всё время")
    }

    var selectedHabit by remember {
        mutableStateOf("Бег")
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
                selectedPeriod = it
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
                    value = "438",
                    label = "Выполнено",
                    modifier = Modifier.weight(1f)
                )

                StatisticsValue(
                    value = "12 ч 40 мин",
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
                    value = "82%",
                    label = "Успешность",
                    modifier = Modifier.weight(1f)
                )

                StatisticsValue(
                    value = "42 мин",
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

        MonthlyComparisonCard()

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
            selectedHabit = selectedHabit,
            onHabitSelected = {
                selectedHabit = it
            }
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        HabitStatisticsCard(
            habitName = selectedHabit
        )

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
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {

    val periods = listOf(
        "Всё время",
        "День",
        "Неделя",
        "Месяц",
        "Год"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            periods.take(3).forEach { period ->

                if (period == selectedPeriod) {

                    Button(
                        onClick = {
                            onPeriodSelected(period)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(period)
                    }

                } else {

                    OutlinedButton(
                        onClick = {
                            onPeriodSelected(period)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(period)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            periods.drop(3).forEach { period ->

                if (period == selectedPeriod) {

                    Button(
                        onClick = {
                            onPeriodSelected(period)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(period)
                    }

                } else {

                    OutlinedButton(
                        onClick = {
                            onPeriodSelected(period)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(period)
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
fun MonthlyComparisonCard() {

    FullStatisticsCard {

        Text(
            text = "Август → Сентябрь",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        ComparisonRow(
            title = "Выполнено",
            oldValue = "112",
            newValue = "138",
            change = "+23%"
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ComparisonRow(
            title = "Время",
            oldValue = "8 ч 40 мин",
            newValue = "10 ч 20 мин",
            change = "+19%"
        )
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
    selectedHabit: String,
    onHabitSelected: (String) -> Unit
) {

    FullStatisticsCard {

        Text(
            text = selectedHabit,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "Бег",
                "Чтение",
                "Учёба"
            ).forEach { habit ->

                if (habit == selectedHabit) {

                    Button(
                        onClick = {
                            onHabitSelected(habit)
                        }
                    ) {
                        Text(habit)
                    }

                } else {

                    OutlinedButton(
                        onClick = {
                            onHabitSelected(habit)
                        }
                    ) {
                        Text(habit)
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
    habitName: String
) {

    FullStatisticsCard {

        Text(
            text = habitName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            StatisticsValue(
                value = "12 дней",
                label = "Текущая серия",
                modifier = Modifier.weight(1f)
            )

            StatisticsValue(
                value = "24 дня",
                label = "Лучшая серия",
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
                value = "87%",
                label = "Выполнение",
                modifier = Modifier.weight(1f)
            )

            StatisticsValue(
                value = "38 мин",
                label = "Среднее время",
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
                value = "116 ч",
                label = "Общее время",
                modifier = Modifier.weight(1f)
            )

            StatisticsValue(
                value = "32 мин",
                label = "В среднем в день",
                modifier = Modifier.weight(1f)
            )
        }
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