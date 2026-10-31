package com.example.habittracker.ui.screens


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.example.habittracker.ui.components.HabitCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittracker.viewmodel.HabitViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.habittracker.ui.components.AddHabitDialog
import com.example.habittracker.ui.components.HabitBottomSheet
import com.example.habittracker.ui.components.WeekSelector
import com.example.habittracker.ui.model.HabitUiModel
import com.example.habittracker.viewmodel.HabitViewModelFactory
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.Alignment
import com.example.habittracker.widget.WidgetUpdater
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset


@Composable
fun HabitTrackerScreen(
    modifier: Modifier = Modifier,
    initialHabitId: Int = -1
) {
    var showHabitMenu by remember {
        mutableStateOf(false)
    }

    var showPauseDialog by remember {
        mutableStateOf(false)
    }

    var showResetStreakDialog by remember {
        mutableStateOf(false)
    }

    var pauseIndefinitely by remember {
        mutableStateOf(false)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var selectedHabit by remember {
        mutableStateOf<HabitUiModel?>(null)
    }

    val context = LocalContext.current

    val viewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(context)
    )

    val weekStart by viewModel.weekStart.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val habits by viewModel.habits.collectAsState(emptyList())

    LaunchedEffect(initialHabitId, habits) {

        if (
            initialHabitId != -1 &&
            selectedHabit == null
        ) {
            habits.find {
                it.id == initialHabitId
            }?.let { habit ->

                selectedHabit = habit
                showBottomSheet = true

            }
        }

    }
    Scaffold(

        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    selectedHabit = null
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить привычку"
                )
            }

        }

    ) { innerPadding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            WeekSelector(

                weekStart = weekStart,

                selectedDate = selectedDate,

                onPreviousWeek = {
                    viewModel.previousWeek()
                },

                onNextWeek = {
                    viewModel.nextWeek()
                },

                onDateSelected = {
                    viewModel.selectDate(it)
                }

            )

            Spacer(modifier = Modifier.height(20.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Text("Habit Tracker")
            Text("Количество привычек: ${habits.size}")
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),

                contentPadding = PaddingValues(
                    vertical = 4.dp
                )
            ) {

                val sortedHabits = habits.sortedByDescending {
                    it.completed
                }

                itemsIndexed(
                    items = sortedHabits,
                    key = { _, habit ->
                        habit.id
                    }
                ) { index, habit ->

                    val nextHabit =
                        sortedHabits.getOrNull(index + 1)

                    HabitCard(
                        modifier = Modifier
                            .animateItem(),

                        habit = habit,

                        completed = habit.completed,

                        onClick = {
                            selectedHabit = habit
                            showBottomSheet = true
                        },

                        onMenuClick = {
                            selectedHabit = habit
                            showHabitMenu = true
                        }
                    )

                    if (
                        habit.completed &&
                        nextHabit != null &&
                        !nextHabit.completed
                    ) {

                        HorizontalDivider(
                            modifier = Modifier
                                .padding(
                                    horizontal = 24.dp,
                                    vertical = 8.dp
                                ),

                            color = MaterialTheme
                                .colorScheme
                                .outlineVariant
                        )
                    }
                }
            }


            if (showDialog) {
                AddHabitDialog(
                    habit = selectedHabit,
                    onDismiss = {
                        showDialog = false
                    },

                    onConfirm = {
                            name,
                            icon,
                            color,
                            habitType,
                            guideline,
                            category,
                            priority
                        ->
                        if (selectedHabit == null) {
                            viewModel.addHabit(
                                name = name,
                                icon = icon,
                                color = color,
                                habitType = habitType,
                                guideline = guideline,
                                category = category,
                                priority = priority
                            )
                        } else {
                            viewModel.updateHabit(
                                id = selectedHabit!!.id,
                                name = name,
                                icon = icon,
                                color = color,
                                habitType = habitType,
                                guideline = guideline,
                                category = category,
                                priority = priority
                            )
                        }

                        showDialog = false
                    },

                    onDelete = {
                        selectedHabit?.let {
                            viewModel.deleteHabit(it.id)
                        }
                        showDialog = false
                    }
                )
            }
            if (showHabitMenu && selectedHabit != null) {
                val habit = selectedHabit!!

                AlertDialog(
                    onDismissRequest = {
                        showHabitMenu = false
                    },
                    title = {
                        Text(habit.name)
                    },
                    text = {
                        Column {
                            TextButton(
                                onClick = {
                                    showHabitMenu = false
                                    showDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Редактировать",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (habit.isPaused) {
                                TextButton(
                                    onClick = {
                                        showHabitMenu = false
                                        viewModel.resumeHabit(habit.id)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Возобновить",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            } else {
                                TextButton(
                                    onClick = {
                                        showHabitMenu = false
                                        pauseIndefinitely = false
                                        showPauseDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Приостановить",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            TextButton(
                                onClick = {
                                    showHabitMenu = false
                                    showResetStreakDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Сбросить серию",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showHabitMenu = false
                            }
                        ) {
                            Text("Закрыть")
                        }
                    }
                )
            }
            if (showPauseDialog && selectedHabit != null) {
                val todayMillis = LocalDate.now()
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli()

                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = todayMillis
                )

                val selectedEndDate = datePickerState.selectedDateMillis?.let {
                    LocalDate.ofEpochDay(it / 86_400_000L)
                }

                DatePickerDialog(
                    onDismissRequest = {
                        showPauseDialog = false
                    },
                    confirmButton = {
                        TextButton(
                            enabled = pauseIndefinitely ||
                                    (selectedEndDate != null &&
                                            !selectedEndDate.isBefore(LocalDate.now())),
                            onClick = {
                                val endDate = if (pauseIndefinitely) {
                                    null
                                } else {
                                    selectedEndDate
                                }

                                viewModel.pauseHabit(
                                    habitId = selectedHabit!!.id,
                                    endDate = endDate
                                )

                                showPauseDialog = false
                            }
                        ) {
                            Text("Приостановить")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showPauseDialog = false
                            }
                        ) {
                            Text("Отмена")
                        }
                    }
                ) {
                    Column {
                        DatePicker(
                            state = datePickerState
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = pauseIndefinitely,
                                onCheckedChange = {
                                    pauseIndefinitely = it
                                }
                            )

                            Text("Бессрочная пауза")
                        }
                    }
                }
            }
            if (showResetStreakDialog && selectedHabit != null) {
                AlertDialog(
                    onDismissRequest = {
                        showResetStreakDialog = false
                    },
                    title = {
                        Text("Сбросить серию?")
                    },
                    text = {
                        Text(
                            "Текущая серия будет сброшена. " +
                                    "История выполнений и лучшая серия сохранятся."
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.resetHabitStreak(
                                    selectedHabit!!.id
                                )
                                showResetStreakDialog = false
                            }
                        ) {
                            Text("Сбросить")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showResetStreakDialog = false
                            }
                        ) {
                            Text("Отмена")
                        }
                    }
                )
            }
            if (showBottomSheet && selectedHabit != null) {
                HabitBottomSheet(
                    habit = selectedHabit!!,
                    onDismiss = {
                        showBottomSheet = false
                    },

                    onEntryChanged = { completed, minutes, count ->
                        viewModel.updateHabitEntry(
                            habitId = selectedHabit!!.id,
                            completed = completed,
                            minutesSpent = minutes,
                            count = count
                        )
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HabitTrackerScreenPreview() {
    HabitTrackerTheme {
        HabitTrackerScreen()
    }
}