
package com.example.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.HabitUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitBottomSheet(
    habit: HabitUiModel,
    onDismiss: () -> Unit,
    onEntryChanged: (Boolean, Int, Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    var completed by remember(habit.id) {
        mutableStateOf(habit.completed)
    }
    var minutes by remember(habit.id) {
        mutableStateOf(habit.minutesSpent.toString())
    }
    var count by remember(habit.id) {
        mutableStateOf(habit.count.toString())
    }

    val habitColor = Color(habit.color)
    val colorScheme = MaterialTheme.colorScheme

    val typeLabel = when (habit.habitType) {
        "CHECK" -> "Выполнение"
        "COUNTER" -> "Количество"
        else -> "Время"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 32.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(habitColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = habit.icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = habitColor
                    )
                }
            }

            if (!habit.guideline.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = habitColor.copy(alpha = 0.08f)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            text = "Мой ориентир",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = habit.guideline,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            when (habit.habitType) {
                "CHECK" -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (completed) habitColor.copy(alpha = 0.12f)
                                else colorScheme.surfaceVariant
                            )
                            .clickable {
                                completed = !completed
                                onEntryChanged(
                                    completed,
                                    habit.minutesSpent,
                                    habit.count
                                )
                            }
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (completed) "Выполнено" else "Не выполнено",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (completed) habitColor
                                else colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (completed) "Привычка отмечена"
                                else "Нажмите, чтобы отметить выполнение",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = completed,
                            onCheckedChange = {
                                completed = it
                                onEntryChanged(
                                    completed,
                                    habit.minutesSpent,
                                    habit.count
                                )
                            }
                        )
                    }
                }

                "COUNTER" -> {
                    Text(
                        text = "Количество",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    NumberInput(
                        value = count,
                        onValueChange = { newValue ->
                            count = newValue
                            val value = newValue.toIntOrNull() ?: 0
                            completed = value > 0
                            onEntryChanged(
                                completed,
                                habit.minutesSpent,
                                value.coerceAtLeast(0)
                            )
                        },
                        placeholder = "Введите количество",
                        suffix = "шт.",
                        accentColor = habitColor
                    )
                }

                else -> {
                    Text(
                        text = "Время выполнения",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15, 30, 60, 120).forEach { value ->
                            val selected = minutes.toIntOrNull() == value

                            Button(
                                onClick = {
                                    minutes = value.toString()
                                    completed = value > 0
                                    onEntryChanged(
                                        completed,
                                        value,
                                        habit.count
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(
                                    horizontal = 4.dp,
                                    vertical = 10.dp
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) habitColor
                                    else colorScheme.surfaceVariant,
                                    contentColor = if (selected) Color.White
                                    else colorScheme.onSurfaceVariant
                                )
                            ) {
                                Text("$value")
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    NumberInput(
                        value = minutes,
                        onValueChange = { newValue ->
                            minutes = newValue
                            val value = newValue.toIntOrNull() ?: 0
                            completed = value > 0
                            onEntryChanged(
                                completed,
                                value.coerceAtLeast(0),
                                habit.count
                            )
                        },
                        placeholder = "Введите время",
                        suffix = "мин",
                        accentColor = habitColor
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    suffix: String,
    accentColor: Color
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = { input ->
                if (input.isEmpty() || input.all { it.isDigit() }) {
                    onValueChange(input)
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .width(150.dp)
                .drawBehind {
                    drawLine(
                        color = if (value.isNotEmpty()) accentColor
                        else colorScheme.outline,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                    innerTextField()
                }
            }
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = suffix,
            color = colorScheme.onSurfaceVariant
        )
    }
}
