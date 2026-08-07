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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AddHabitDialog(
    habit: HabitUiModel? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long) -> Unit,
    onDelete: () -> Unit = {}
) {

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

    val habitColor = Color(selectedColor)

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(),

            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {

                // Заголовок

                Text(
                    text =
                        if (habit == null)
                            "Новая привычка"
                        else
                            "Редактирование",

                    style =
                        MaterialTheme.typography.headlineSmall,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // Предпросмотр

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                habitColor.copy(
                                    alpha = 0.15f
                                )
                            )
                            .border(
                                width = 2.dp,
                                color = habitColor,
                                shape = CircleShape
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = selectedIcon,
                            style =
                                MaterialTheme.typography
                                    .headlineLarge
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // Название

                OutlinedTextField(
                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text("Название")
                    },

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // Иконка

                Text(
                    text = "Иконка",
                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
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
                                    if (selected)
                                        habitColor.copy(alpha = 0.15f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = if (selected) 2.dp else 0.dp,
                                    color = if (selected)
                                        habitColor
                                    else
                                        Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedIcon = icon
                                },

                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = icon,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // Цвет

                Text(
                    text = "Цвет",
                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {

                    items(colors) { color ->

                        val selected = color == selectedColor

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(color)
                                )
                                .border(
                                    width = if (selected) 3.dp else 0.dp,

                                    color =
                                        if (selected)
                                            MaterialTheme
                                                .colorScheme
                                                .onSurface
                                        else
                                            Color.Transparent,

                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedColor = color
                                }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                // Кнопки

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    if (habit != null) {

                        TextButton(
                            onClick = onDelete,

                            colors =
                                ButtonDefaults
                                    .textButtonColors(
                                        contentColor =
                                            MaterialTheme
                                                .colorScheme
                                                .error
                                    )
                        ) {
                            Text("Удалить")
                        }
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Отмена")
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Button(
                        onClick = {

                            onConfirm(
                                name,
                                selectedIcon,
                                selectedColor
                            )
                        },

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    habitColor
                            )
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
