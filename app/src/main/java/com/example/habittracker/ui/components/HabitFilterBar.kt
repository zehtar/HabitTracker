package com.example.habittracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun HabitFilterBar(
    selectedCategory: String,
    selectedPriority: String,
    selectedType: String,
    selectedStatus: String,
    onCategorySelected: (String) -> Unit,
    onPrioritySelected: (String) -> Unit,
    onTypeSelected: (String) -> Unit,
    onStatusSelected: (String) -> Unit,
    onClear: () -> Unit,
    hasActiveFilters: Boolean
) {
    val categories = listOf(
        "ALL" to "Все категории",
        "NONE" to "Без категории",
        "WORK_DEVELOPMENT" to "Работа и развитие",
        "HEALTH" to "Здоровье",
        "LEARNING" to "Обучение",
        "HOBBY" to "Хобби",
        "HOME_EVERYDAY" to "Быт",
        "PERSONAL_DEVELOPMENT" to "Личное развитие"
    )
    val priorities = listOf(
        "ALL" to "Любой приоритет",
        "MANDATORY" to "Обязательная",
        "IMPORTANT" to "Важная",
        "ADDITIONAL" to "Дополнительная"
    )
    val types = listOf(
        "ALL" to "Любой тип",
        "CHECK" to "Выполнение",
        "TIME" to "Время",
        "COUNTER" to "Количество"
    )
    val statuses = listOf(
        "ALL" to "Любой статус",
        "ACTIVE" to "Активные",
        "PAUSED" to "На паузе"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            HabitFilterDropdown(
                modifier = Modifier,
                value = selectedCategory,
                options = categories,
                onSelected = onCategorySelected
            )
            HabitFilterDropdown(
                modifier = Modifier,
                value = selectedPriority,
                options = priorities,
                onSelected = onPrioritySelected
            )
            HabitFilterDropdown(
                modifier = Modifier,
                value = selectedType,
                options = types,
                onSelected = onTypeSelected
            )
            HabitFilterDropdown(
                modifier = Modifier,
                value = selectedStatus,
                options = statuses,
                onSelected = onStatusSelected
            )
        }
        if (hasActiveFilters) {
            TextButton(onClick = onClear) {
                Text("Сбросить фильтры")
            }
        }
    }
}

@Composable
private fun HabitFilterDropdown(
    modifier: Modifier,
    value: String,
    options: List<Pair<String, String>>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val label = options.firstOrNull { it.first == value }?.second.orEmpty()

    androidx.compose.foundation.layout.Box(
        modifier = modifier.widthIn(min = 112.dp)
    ) {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (key, title) ->
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = {
                        onSelected(key)
                        expanded = false
                    }
                )
            }
        }
    }
}
