package com.example.habittracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittracker.data.database.entity.HabitEntity
import com.example.habittracker.data.database.entity.HabitReminderEntity
import com.example.habittracker.viewmodel.HabitViewModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import java.util.Calendar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(context)
    )

    val habits by viewModel
        .allHabits
        .collectAsState(initial = emptyList())

    val reminders by viewModel
        .allReminders
        .collectAsState(initial = emptyList())

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var editingReminder by remember {
        mutableStateOf<HabitReminderEntity?>(null)
    }

    // остальной код RemindersScreen

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Напоминания")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Text("‹")
                    }
                }
            )
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {
                    showAddDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить"
                )
            }
        }
    ) { paddingValues ->

        if (reminders.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Нет напоминаний",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Добавьте напоминание для привычки",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                item {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }

                items(
                    items = reminders,
                    key = {
                        it.id
                    }
                ) { reminder ->

                    val habit = habits.find {
                        it.id == reminder.habitId
                    }

                    if (habit != null) {

                        ReminderCard(
                            habit = habit,
                            reminder = reminder,
                            onToggle = {
                                viewModel.toggleReminder(
                                    reminder
                                )
                            },
                            onClick = {
                                editingReminder = reminder
                            },
                            onDelete = {
                                viewModel.deleteReminder(
                                    reminder
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(80.dp)
                    )
                }
            }
        }
    }

    if (showAddDialog) {

        ReminderDialog(
            habits = habits,
            reminder = null,
            onDismiss = {
                showAddDialog = false
            },
            onSave = {
                    habitId,
                    hour,
                    minute,
                    requireCompletion ->

                viewModel.addReminder(
                    habitId = habitId,
                    hour = hour,
                    minute = minute,
                    requireCompletion = requireCompletion
                )

                showAddDialog = false
            }
        )
    }

    editingReminder?.let { reminder ->

        ReminderDialog(
            habits = habits,
            reminder = reminder,
            onDismiss = {
                editingReminder = null
            },
            onSave = {
                    habitId,
                    hour,
                    minute,
                    requireCompletion ->

                viewModel.updateReminder(
                    reminder.copy(
                        habitId = habitId,
                        hour = hour,
                        minute = minute,
                        requireCompletion = requireCompletion
                    )
                )

                editingReminder = null
            }
        )
    }
}
@Composable
private fun ReminderCard(
    habit: HabitEntity,
    reminder: HabitReminderEntity,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = habit.icon,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.padding(
                    horizontal = 6.dp
                )
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = habit.name,
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formatReminderTime(
                        reminder.hour,
                        reminder.minute
                    ),
                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }

            Switch(
                checked = reminder.enabled,
                onCheckedChange = {
                    onToggle()
                }
            )

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить"
                )
            }
        }
    }
}
private fun formatReminderTime(
    hour: Int,
    minute: Int
): String {
    return "%02d:%02d".format(
        hour,
        minute
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialog(
    habits: List<HabitEntity>,
    reminder: HabitReminderEntity?,
    onDismiss: () -> Unit,
    onSave: (
        habitId: Int,
        hour: Int,
        minute: Int,
        requireCompletion: Boolean
    ) -> Unit
) {
    val calendar = Calendar.getInstance()

    var selectedHabitId by remember {
        mutableStateOf(
            reminder?.habitId ?: habits.firstOrNull()?.id
        )
    }

    var requireCompletion by remember {
        mutableStateOf(reminder?.requireCompletion ?: false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = reminder?.hour
            ?: calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = reminder?.minute
            ?: calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    val selectedHabit = habits.find {
        it.id == selectedHabitId
    }

    val isEditing = reminder != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) {
                    "Редактирование"
                } else {
                    "Новое напоминание"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Выбор привычки
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Привычка",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = it
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable {
                                    expanded = true
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (selectedHabit != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                Color(selectedHabit.color)
                                                    .copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = selectedHabit.icon,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = selectedHabit.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Выбранная привычка",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Нет доступных привычек",
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Выбрать привычку",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {
                            habits.forEach { habit ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        Color(habit.color)
                                                            .copy(alpha = 0.15f)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = habit.icon
                                                )
                                            }

                                            Text(
                                                text = habit.name,
                                                fontWeight =
                                                    if (habit.id == selectedHabitId) {
                                                        FontWeight.SemiBold
                                                    } else {
                                                        FontWeight.Normal
                                                    }
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedHabitId = habit.id
                                        expanded = false
                                    },
                                    trailingIcon = {
                                        if (habit.id == selectedHabitId) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Выбрано",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Выбор времени
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Время напоминания",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showTimePicker = true
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = String.format(
                                        "%02d:%02d",
                                        timePickerState.hour,
                                        timePickerState.minute
                                    ),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ежедневно",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "Изменить",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Обязательное выполнение
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (requireCompletion) {
                                MaterialTheme.colorScheme.primaryContainer
                                    .copy(alpha = 0.45f)
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerLow
                            }
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (requireCompletion) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                requireCompletion = !requireCompletion
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (requireCompletion) {
                                        MaterialTheme.colorScheme.primary
                                            .copy(alpha = 0.12f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = if (requireCompletion) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Требовать выполнения",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Уведомление будет возвращаться, пока привычка не выполнена",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = requireCompletion,
                            onCheckedChange = {
                                requireCompletion = it
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = selectedHabitId != null,
                onClick = {
                    val habitId = selectedHabitId ?: return@Button

                    onSave(
                        habitId,
                        timePickerState.hour,
                        timePickerState.minute,
                        requireCompletion
                    )

                    onDismiss()
                }
            ) {
                Text(
                    text = if (isEditing) {
                        "Сохранить"
                    } else {
                        "Создать"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        }
    )

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = {
                showTimePicker = false
            },
            title = {
                Text("Выберите время")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) {
                    Text("Готово")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        ) {
            TimePicker(
                state = timePickerState
            )
        }
    }
}