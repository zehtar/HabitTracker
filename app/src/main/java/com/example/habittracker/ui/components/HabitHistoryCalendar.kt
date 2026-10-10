
package com.example.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.HabitCalendarDayUiModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class HabitHistoryDisplayMode {
    COMPLETION,
    TIME
}

@Composable
fun HabitHistoryCalendar(
    days: List<HabitCalendarDayUiModel>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()

    var displayedMonth by remember {
        mutableStateOf(YearMonth.from(today))
    }

    var displayMode by remember {
        mutableStateOf(HabitHistoryDisplayMode.COMPLETION)
    }

    var selectedDate by remember {
        mutableStateOf(today)
    }

    val entriesByDate = remember(days) {
        days.associateBy { it.date }
    }

    val monthStart = displayedMonth.atDay(1)
    val daysInMonth = displayedMonth.lengthOfMonth()
    val leadingEmptyDays =
        monthStart.dayOfWeek.value - DayOfWeek.MONDAY.value

    val totalCells = ((leadingEmptyDays + daysInMonth + 6) / 7) * 7

    val monthFormatter = remember {
        DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))
    }

    val selectedEntry = entriesByDate[selectedDate]
    val selectedDateIsFuture = selectedDate.isAfter(today)

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        displayedMonth = displayedMonth.minusMonths(1)
                        selectedDate = displayedMonth.atEndOfMonth()
                            .let { if (it.isAfter(today)) today else it }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Предыдущий месяц"
                    )
                }

                Text(
                    text = displayedMonth.format(monthFormatter)
                        .replaceFirstChar { it.titlecase(Locale("ru")) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        if (displayedMonth.isBefore(YearMonth.from(today))) {
                            displayedMonth = displayedMonth.plusMonths(1)
                            selectedDate = today
                        }
                    },
                    enabled = displayedMonth.isBefore(YearMonth.from(today))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Следующий месяц"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        displayMode = HabitHistoryDisplayMode.COMPLETION
                    }
                ) {
                    Text(
                        text = "Выполнение",
                        color = if (displayMode == HabitHistoryDisplayMode.COMPLETION) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (displayMode == HabitHistoryDisplayMode.COMPLETION) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }

                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        displayMode = HabitHistoryDisplayMode.TIME
                    }
                ) {
                    Text(
                        text = "Время",
                        color = if (displayMode == HabitHistoryDisplayMode.TIME) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (displayMode == HabitHistoryDisplayMode.TIME) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val weekdays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                weekdays.forEach { weekday ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = weekday,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            for (weekStart in 0 until totalCells step 7) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (column in 0..6) {
                        val cellIndex = weekStart + column
                        val dayNumber = cellIndex - leadingEmptyDays + 1

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayNumber in 1..daysInMonth) {
                                val date = displayedMonth.atDay(dayNumber)
                                val entry = entriesByDate[date]
                                val isFuture = date.isAfter(today)
                                val isSelected = date == selectedDate
                                val isToday = date == today

                                val backgroundColor = when {
                                    isFuture -> Color.Transparent

                                    displayMode == HabitHistoryDisplayMode.COMPLETION -> {
                                        when {
                                            entry == null ->
                                                MaterialTheme.colorScheme.surfaceVariant
                                                    .copy(alpha = 0.45f)

                                            entry.completed ->
                                                MaterialTheme.colorScheme.primary

                                            else ->
                                                MaterialTheme.colorScheme.errorContainer
                                        }
                                    }

                                    else -> {
                                        val minutes = entry?.minutes ?: 0

                                        when {
                                            minutes <= 0 ->
                                                MaterialTheme.colorScheme.surfaceVariant
                                                    .copy(alpha = 0.45f)

                                            minutes < 30 ->
                                                MaterialTheme.colorScheme.primary
                                                    .copy(alpha = 0.30f)

                                            minutes < 60 ->
                                                MaterialTheme.colorScheme.primary
                                                    .copy(alpha = 0.55f)

                                            else ->
                                                MaterialTheme.colorScheme.primary
                                        }
                                    }
                                }

                                val textColor = when {
                                    isFuture ->
                                        MaterialTheme.colorScheme.onSurface

                                    displayMode == HabitHistoryDisplayMode.COMPLETION &&
                                            entry?.completed == true ->
                                        MaterialTheme.colorScheme.onPrimary

                                    displayMode == HabitHistoryDisplayMode.COMPLETION &&
                                            entry?.completed == false ->
                                        MaterialTheme.colorScheme.onErrorContainer

                                    displayMode == HabitHistoryDisplayMode.TIME &&
                                            (entry?.minutes ?: 0) >= 30 ->
                                        MaterialTheme.colorScheme.onPrimary

                                    else ->
                                        MaterialTheme.colorScheme.onSurface
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(backgroundColor)
                                        .then(
                                            if (isSelected || isToday) {
                                                Modifier.border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) {
                                                        MaterialTheme.colorScheme.tertiary
                                                    } else {
                                                        MaterialTheme.colorScheme.outline
                                                    },
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .then(
                                            if (!isFuture) {
                                                Modifier.clickable {
                                                    selectedDate = date
                                                }
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        color = textColor,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isToday || isSelected) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (displayMode) {
                HabitHistoryDisplayMode.COMPLETION -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LegendDot(MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Выполнено",
                            style = MaterialTheme.typography.bodySmall
                        )

                        LegendDot(MaterialTheme.colorScheme.errorContainer)
                        Text(
                            text = "Не выполнено",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                HabitHistoryDisplayMode.TIME -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LegendDot(MaterialTheme.colorScheme.surfaceVariant)
                        Text(
                            text = "0 мин",
                            style = MaterialTheme.typography.bodySmall
                        )

                        LegendDot(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)
                        )
                        Text(
                            text = "До 30",
                            style = MaterialTheme.typography.bodySmall
                        )

                        LegendDot(MaterialTheme.colorScheme.primary)
                        Text(
                            text = "60+",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(12.dp)
            ) {
                Text(
                    text = selectedDate.format(dateFormatter)
                        .replaceFirstChar { it.titlecase(Locale("ru")) },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (selectedDateIsFuture) {
                    Text(
                        text = "На эту дату данных ещё нет.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = when {
                            selectedEntry == null ->
                                "Нет сохранённой записи за этот день."

                            selectedEntry.completed ->
                                "Привычка выполнена."

                            else ->
                                "Привычка не выполнена."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Затрачено времени: ${selectedEntry?.minutes ?: 0} мин",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
}
