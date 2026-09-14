package com.example.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.CalendarDayUiModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MonthlyCalendar(
    month: LocalDate,
    days: List<CalendarDayUiModel>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (CalendarDayUiModel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val monthFormatter = DateTimeFormatter.ofPattern(
        "LLLL yyyy",
        Locale("ru")
    )

    val firstDay = month.withDayOfMonth(1)

    val startOffset =
        firstDay.dayOfWeek.value - DayOfWeek.MONDAY.value

    val totalCells =
        ((startOffset + month.lengthOfMonth() + 6) / 7) * 7

    val dayMap = days.associateBy {
        it.date.dayOfMonth
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = onPreviousMonth
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Предыдущий месяц"
                )
            }

            Text(
                text = month
                    .format(monthFormatter)
                    .replaceFirstChar {
                        it.uppercase()
                    },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onNextMonth
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Следующий месяц"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        val weekDays = listOf(
            "Пн",
            "Вт",
            "Ср",
            "Чт",
            "Пт",
            "Сб",
            "Вс"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        repeat(totalCells / 7) { week ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                repeat(7) { dayOfWeek ->

                    val cellIndex =
                        week * 7 + dayOfWeek

                    val dayNumber =
                        cellIndex - startOffset + 1

                    if (
                        dayNumber in 1..month.lengthOfMonth()
                    ) {
                        val day = dayMap[dayNumber]

                        CalendarDay(
                            day = day,
                            onClick = {
                                day?.let(onDayClick)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .size(42.dp)
                        )
                    }
                }
            }

            if (week < totalCells / 7 - 1) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        CalendarLegend()
    }
}

@Composable
private fun CalendarDay(
    day: CalendarDayUiModel?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        calendarActivityColor(
            completionPercent =
                day?.completionPercent ?: 0
        )

    Box(
        modifier = modifier
            .padding(horizontal = 3.dp)
            .size(42.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )
            .background(backgroundColor)
            .clickable(
                enabled = day != null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day?.date?.dayOfMonth?.toString() ?: "",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color =
                if (
                    day != null &&
                    day.completionPercent >= 50
                ) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
        )
    }
}

@Composable
private fun calendarActivityColor(
    completionPercent: Int
): Color {
    val primary =
        MaterialTheme.colorScheme.primary

    return when {
        completionPercent == 0 ->
            MaterialTheme.colorScheme.surfaceContainerHighest

        completionPercent <= 25 ->
            primary.copy(alpha = 0.30f)

        completionPercent <= 50 ->
            primary.copy(alpha = 0.50f)

        completionPercent <= 75 ->
            primary.copy(alpha = 0.75f)

        else ->
            primary
    }
}

@Composable
private fun CalendarLegend() {
    val primary =
        MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        LegendItem(
            color = MaterialTheme
                .colorScheme
                .surfaceContainerHighest,
            text = "0%"
        )

        LegendItem(
            color = primary.copy(alpha = 0.30f),
            text = "1–25%"
        )

        LegendItem(
            color = primary.copy(alpha = 0.50f),
            text = "26–50%"
        )

        LegendItem(
            color = primary.copy(alpha = 0.75f),
            text = "51–75%"
        )

        LegendItem(
            color = primary,
            text = "76–100%"
        )
    }
}
@Composable
private fun LegendItem(
    color: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            horizontal = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(
                    RoundedCornerShape(3.dp)
                )
                .background(color)
        )

        Spacer(
            modifier = Modifier.size(4.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme
                .colorScheme
                .onSurfaceVariant
        )
    }
}