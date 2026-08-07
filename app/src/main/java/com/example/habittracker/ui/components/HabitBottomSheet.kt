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
    onEntryChanged: (
        Boolean,
        Int
    ) -> Unit
) {

    val sheetState = rememberModalBottomSheetState()

    var completed by remember {
        mutableStateOf(habit.completed)
    }

    var minutes by remember {
        mutableStateOf(habit.minutesSpent.toString())
    }

    val habitColor = Color(habit.color)

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

            // Иконка и название
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            habitColor.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = habit.icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = if (completed)
                            "Привычка выполнена"
                        else
                            "Привычка ещё не выполнена",

                        style = MaterialTheme.typography.bodyMedium,

                        color =
                            if (completed)
                                habitColor
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (completed)
                            habitColor.copy(alpha = 0.12f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable {
                        completed = !completed

                        onEntryChanged(
                            completed,
                            minutes.toIntOrNull() ?: 0
                        )
                    }
                    .padding(
                        horizontal = 20.dp,
                        vertical = 18.dp
                    )
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = if (completed)
                                "Выполнено"
                            else
                                "Не выполнено",

                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,

                            color =
                                if (completed)
                                    habitColor
                                else
                                    MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = if (completed)
                                "Привычка отмечена"
                            else
                                "Нажмите, чтобы выполнить",

                            style = MaterialTheme.typography.bodyMedium,

                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = completed,

                        onCheckedChange = {

                            completed = it

                            onEntryChanged(
                                completed,
                                minutes.toIntOrNull() ?: 0
                            )
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )



            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // Время

            Text(
                text = "Время выполнения",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                listOf(15, 30, 60, 120).forEach { value ->

                    val selected =
                        minutes.toIntOrNull() == value

                    Button(
                        onClick = {

                            minutes = value.toString()

                            onEntryChanged(
                                completed,
                                value
                            )
                        },

                        modifier = Modifier.weight(1f),

                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (selected)
                                    habitColor
                                else
                                    MaterialTheme.colorScheme
                                        .surfaceVariant,

                            contentColor =
                                if (selected)
                                    Color.White
                                else
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                        )
                    ) {

                        Text(
                            text = "$value"
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            val colorScheme = MaterialTheme.colorScheme

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                BasicTextField(
                    value = minutes,

                    onValueChange = {

                        minutes = it

                        onEntryChanged(
                            completed,
                            it.toIntOrNull() ?: 0
                        )
                    },

                    singleLine = true,

                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    ),

                    modifier = Modifier
                        .width(140.dp)
                        .drawBehind {

                            val lineColor =
                                if (minutes.isNotEmpty())
                                    habitColor
                                else
                                    colorScheme.outline

                            drawLine(
                                color = lineColor,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 2.dp.toPx()
                            )
                        },

                    decorationBox = { innerTextField ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Box {
                                if (minutes.isEmpty()) {
                                    Text(
                                        text = "Минут",
                                        color = colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                innerTextField()
                            }

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = "мин",
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }
    }
}