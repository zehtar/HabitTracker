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
            onSave = { habitId, hour, minute ->

                viewModel.addReminder(
                    habitId = habitId,
                    hour = hour,
                    minute = minute
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
            onSave = { habitId, hour, minute ->

                viewModel.updateReminder(
                    reminder.copy(
                        habitId = habitId,
                        hour = hour,
                        minute = minute
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
        minute: Int
    ) -> Unit
) {
    val calendar = Calendar.getInstance()

    var selectedHabitId by remember {
        mutableStateOf(
            reminder?.habitId
                ?: habits.firstOrNull()?.id
        )
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = reminder?.hour
            ?: calendar.get(Calendar.HOUR_OF_DAY),

        initialMinute = reminder?.minute
            ?: calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,



        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Привычка",
                    style = MaterialTheme.typography.titleMedium
                )

                habits.forEach { habit ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedHabitId = habit.id
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected = selectedHabitId == habit.id,
                            onClick = {
                                selectedHabitId = habit.id
                            }
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = habit.name
                        )
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Время",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(
                    onClick = {
                        showTimePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = String.format(
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                enabled = selectedHabitId != null,
                onClick = {

                    val habitId =
                        selectedHabitId ?: return@TextButton

                    onSave(
                        habitId,
                        timePickerState.hour,
                        timePickerState.minute
                    )

                    onDismiss()
                }
            ) {
                Text("Сохранить")
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