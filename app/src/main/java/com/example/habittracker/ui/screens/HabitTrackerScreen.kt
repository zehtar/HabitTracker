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
import com.example.habittracker.widget.WidgetUpdater
import java.time.DayOfWeek
import java.time.LocalDate


@Composable
fun HabitTrackerScreen(
    modifier: Modifier = Modifier
) {
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
                            showDialog = true
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

                    onConfirm = { name, icon, color ->

                        if (selectedHabit == null) {

                            viewModel.addHabit(
                                name,
                                icon,
                                color
                            )

                        } else {

                            viewModel.updateHabit(
                                id = selectedHabit!!.id,
                                name = name,
                                icon = icon,
                                color = color
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

            if (showBottomSheet && selectedHabit != null) {

                HabitBottomSheet(

                    habit = selectedHabit!!,

                    onDismiss = {
                        showBottomSheet = false
                    },

                    onEntryChanged = {
                            completed,
                            minutes ->
                        viewModel.updateHabitEntry(

                            habitId = selectedHabit!!.id,
                            completed = completed,
                            minutesSpent = minutes
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