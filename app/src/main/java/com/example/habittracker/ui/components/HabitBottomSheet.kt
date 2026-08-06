package com.example.habittracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    ModalBottomSheet(

        onDismissRequest = onDismiss,

        sheetState = sheetState

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            Text(
                text = habit.icon,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = habit.name
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Выполнено")

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

            Spacer(modifier = Modifier.height(24.dp))

            Text("Время")

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                listOf(15, 30, 60, 120).forEach { value ->

                    Button(
                        onClick = {

                            minutes = value.toString()
                            onEntryChanged(completed, value)

                        }
                    ) {
                        Text(value.toString())
                    }

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(

                value = minutes,

                onValueChange = {

                    minutes = it

                    onEntryChanged(

                        completed,

                        it.toIntOrNull() ?: 0

                    )

                },

                label = {
                    Text("Минут")
                },

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(32.dp))

        }

    }

}