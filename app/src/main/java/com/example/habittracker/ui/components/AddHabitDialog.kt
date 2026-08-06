package com.example.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.habittracker.ui.model.HabitUiModel

@Composable
fun AddHabitDialog(
    habit: HabitUiModel? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long) -> Unit,
    onDelete: () -> Unit = {}
){
    var name by remember {
        mutableStateOf(
            habit?.name ?: ""
        )
    }

    var selectedIcon by remember {
        mutableStateOf(
            habit?.icon ?: "🎮"
        )
    }

    var selectedColor by remember {
        mutableStateOf(
            habit?.color ?: 0xFF2196F3
        )
    }

    val icons = listOf(
        "🎮",
        "📚",
        "🎨",
        "💪",
        "🇯🇵",
        "💻",
        "🎵",
        "🏃",
        "☕"
    )

    val colors = listOf(
        0xFF2196F3,
        0xFFF44336,
        0xFF4CAF50,
        0xFFFFEB3B,
        0xFF9C27B0,
        0xFFFF9800
    )

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text =
                        if (habit == null)
                            "Новая привычка"
                        else
                            "Редактирование",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Название")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Иконка",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    icons.forEach { icon ->

                        Button(
                            onClick = {
                                selectedIcon = icon
                            }
                        ) {
                            Text(icon)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Цвет",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    colors.forEach { color ->

                        Button(
                            onClick = {
                                selectedColor = color
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(
                                        Color(color),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    if (habit != null) {

                        TextButton(
                            onClick = {
                                onDelete()
                            }
                        ) {
                            Text("Удалить")
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Отмена")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onConfirm(
                                name,
                                selectedIcon,
                                selectedColor
                            )
                        }
                    ) {
                        Text(
                            if (habit == null)
                                "Создать"
                            else
                                "Сохранить"
                        )
                    }
                }
            }
        }
    }
}
