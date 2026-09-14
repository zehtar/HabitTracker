package com.example.habittracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.CalendarDayHabitUiModel
import com.example.habittracker.ui.model.CalendarDayUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarDayDetailsCard(
    day: CalendarDayUiModel,
    habits: List<CalendarDayHabitUiModel>,
    modifier: Modifier = Modifier
) {
    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "d MMMM",
            Locale("ru")
        )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = day.date
                    .format(dateFormatter)
                    .replaceFirstChar {
                        it.uppercase()
                    },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Text(
                    text =
                        "${day.completed} из ${day.total}",
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                Text(
                    text =
                        "${day.completionPercent}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (habits.isEmpty()) {
                Text(
                    text = "Нет выполненных привычек",
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            } else {
                habits.forEachIndexed { index, habit ->

                    CalendarHabitRow(
                        habit = habit
                    )

                    if (index < habits.lastIndex) {
                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHabitRow(
    habit: CalendarDayHabitUiModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = habit.icon,
            style = MaterialTheme.typography.titleLarge
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (habit.minutesSpent > 0) {
                Text(
                    text = "${habit.minutesSpent} мин",
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }
        }

        Text(
            text = "✓",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(habit.color)
        )
    }
}