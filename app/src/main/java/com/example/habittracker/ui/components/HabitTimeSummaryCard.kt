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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.HabitTimeStatisticUiModel

@Composable
fun HabitTimeSummaryCard(
    statistics: List<HabitTimeStatisticUiModel>,
    modifier: Modifier = Modifier
) {
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

            if (statistics.isEmpty()) {

                Text(
                    text = "Нет затраченного времени",
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

            } else {

                statistics.forEachIndexed { index, statistic ->

                    HabitTimeRow(
                        statistic = statistic
                    )

                    if (index < statistics.lastIndex) {
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
private fun HabitTimeRow(
    statistic: HabitTimeStatisticUiModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = statistic.icon,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = statistic.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = formatMinutes(statistic.minutes),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatMinutes(
    minutes: Int
): String {
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