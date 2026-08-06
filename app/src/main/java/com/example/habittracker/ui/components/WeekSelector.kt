package com.example.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekSelector(
    weekStart: LocalDate,
    selectedDate: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = weekStart.month.getDisplayName(
                TextStyle.FULL,
                Locale("ru")
            ).replaceFirstChar {
                it.uppercase()
            } + " " + weekStart.year,

            style = MaterialTheme.typography.titleMedium,

            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),

            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {


            IconButton(onClick = onPreviousWeek) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Предыдущая неделя"
                )
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                repeat(7) { index ->

                    val date = weekStart.plusDays(index.toLong())

                    DayItem(
                        date = date,
                        selected = date == selectedDate,
                        onClick = {
                            onDateSelected(date)
                        }
                    )

                }

            }

            IconButton(onClick = onNextWeek) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Следующая неделя"
                )
            }
        }
    }
}

@Composable
private fun DayItem(
    date: LocalDate,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (selected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant

    val textColor =
        if (selected)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = date.dayOfWeek
                .getDisplayName(
                    TextStyle.SHORT,
                    Locale("ru")
                )
                .take(2)
                .uppercase(),

            color = textColor,

            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = date.dayOfMonth.toString(),
            color = textColor,
            fontWeight = FontWeight.Bold
        )

    }

}