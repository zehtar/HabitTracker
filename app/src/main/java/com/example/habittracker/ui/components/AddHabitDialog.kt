
package com.example.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.habittracker.ui.model.HabitUiModel

@Composable
fun AddHabitDialog(
    habit: HabitUiModel? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, String, String?) -> Unit,
    onDelete: () -> Unit = {}
) {
    var name by remember(habit?.id) {
        mutableStateOf(habit?.name ?: "")
    }
    var selectedIcon by remember(habit?.id) {
        mutableStateOf(habit?.icon ?: "🎮")
    }
    var selectedColor by remember(habit?.id) {
        mutableLongStateOf(habit?.color ?: 0xFF2196F3)
    }
    var selectedType by remember(habit?.id) {
        mutableStateOf(habit?.habitType ?: "TIME")
    }
    var guideline by remember(habit?.id) {
        mutableStateOf(habit?.guideline.orEmpty())
    }

    val icons = listOf(
        "🎮", "📚", "🎨", "💪", "🇯🇵",
        "💻", "🎵", "🏃", "☕"
    )

    val colors = listOf(
        0xFF2196F3, 0xFFF44336, 0xFF4CAF50,
        0xFFFFEB3B, 0xFF9C27B0, 0xFFFF9800
    )

    val types = listOf(
        "CHECK" to "Выполнение",
        "TIME" to "Время",
        "COUNTER" to "Количество"
    )

    val habitColor = Color(selectedColor)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 650.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = if (habit == null) {
                        "Новая привычка"
                    } else {
                        "Редактирование"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(20.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(habitColor.copy(alpha = 0.15f))
                            .border(2.dp, habitColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedIcon,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Тип привычки",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                types.forEach { (type, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedType = type }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = habitColor
                            )
                        )
                        Column {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = when (type) {
                                    "CHECK" -> "Отмечать выполнение"
                                    "TIME" -> "Записывать минуты"
                                    else -> "Записывать количество"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = guideline,
                    onValueChange = { guideline = it },
                    label = { Text("Мой ориентир") },
                    placeholder = {
                        Text("Например: читать 20 минут")
                    },
                    supportingText = {
                        Text("Не влияет на статистику и серии")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    trailingIcon = {
                        if (guideline.isNotEmpty()) {
                            TextButton(
                                onClick = { guideline = "" }
                            ) {
                                Text("Очистить")
                            }
                        }
                    }
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Иконка",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(icons) { icon ->
                        val selected = icon == selectedIcon

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) {
                                        habitColor.copy(alpha = 0.15f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .border(
                                    width = if (selected) 2.dp else 0.dp,
                                    color = if (selected) {
                                        habitColor
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = CircleShape
                                )
                                .clickable { selectedIcon = icon },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = icon)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Цвет",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(colors) { color ->
                        val selected = color == selectedColor

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .border(
                                    width = if (selected) 3.dp else 0.dp,
                                    color = if (selected) {
                                        MaterialTheme.colorScheme.onSurface
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedColor = color
                                }
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (habit != null) {
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Удалить")
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val normalizedName = name.trim()
                            if (normalizedName.isNotEmpty()) {
                                onConfirm(
                                    normalizedName,
                                    selectedIcon,
                                    selectedColor,
                                    selectedType,
                                    guideline.trim().ifEmpty { null }
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = habitColor
                        )
                    ) {
                        Text(
                            if (habit == null) "Создать" else "Сохранить"
                        )
                    }
                }
            }
        }
    }
}
