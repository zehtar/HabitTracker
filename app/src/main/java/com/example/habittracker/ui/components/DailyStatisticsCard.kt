package com.example.habittracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.DailyStatisticUiModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DailyStatisticsCard(
    statistic: DailyStatisticUiModel,
    modifier: Modifier = Modifier
) {

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "EEEE, d MMMM",
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = statistic.date
                        .format(dateFormatter)
                        .replaceFirstChar {
                            it.uppercase()
                        },

                    style = MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${statistic.completionPercent}%",

                    style = MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold,

                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = {
                    statistic.completionPercent / 100f
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp,
                        bottom = 12.dp
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        "Выполнено: " +
                                "${statistic.completed} " +
                                "из ${statistic.total}",

                    style = MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                Text(
                    text =
                        "${statistic.minutes} мин",

                    style = MaterialTheme.typography.bodyMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}