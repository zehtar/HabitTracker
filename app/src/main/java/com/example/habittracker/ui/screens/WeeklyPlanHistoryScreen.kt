package com.example.habittracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.habittracker.data.database.entity.WeeklyPlanEntity
import com.example.habittracker.data.repository.WeeklyPlanDetails
import com.example.habittracker.viewmodel.WeeklyPlanHistoryViewModel
import com.example.habittracker.viewmodel.WeeklyPlanHistoryViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeeklyPlanHistoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: WeeklyPlanHistoryViewModel = viewModel(
        factory = WeeklyPlanHistoryViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()

    uiState.selectedPlan?.let { details ->
        ApprovedWeeklyPlanDialog(details = details, onDismiss = viewModel::closePlan)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("Назад") }
            Text(
                text = "История планов",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(12.dp))

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator() }
            }
            uiState.plans.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Завершённых недель пока нет", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Утверждённые планы будут появляться здесь.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = uiState.plans, key = { it.id }) { plan ->
                        WeeklyPlanHistoryCard(plan = plan, onClick = { viewModel.openPlan(plan.id) })
                    }
                    if (uiState.error != null) {
                        item { Text(uiState.error!!, color = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
    }

    if (uiState.isLoadingDetails) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text("Загрузка плана…") },
            text = { CircularProgressIndicator() }
        )
    }

    if (uiState.error != null && uiState.plans.isEmpty()) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            confirmButton = {
                TextButton(onClick = viewModel::dismissError) { Text("Закрыть") }
            },
            title = { Text("Ошибка") },
            text = { Text(uiState.error!!) }
        )
    }
}

@Composable
private fun WeeklyPlanHistoryCard(plan: WeeklyPlanEntity, onClick: () -> Unit) {
    val start = LocalDate.parse(plan.weekStartDate)
    val end = LocalDate.parse(plan.weekEndDate)
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("ru"))

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                start.format(formatter) + " — " + end.format(formatter),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Утверждённый план",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            plan.loadRating?.let { rating ->
                Text(
                    loadRatingLabel(rating),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ApprovedWeeklyPlanDialog(details: WeeklyPlanDetails, onDismiss: () -> Unit) {
    val start = LocalDate.parse(details.plan.weekStartDate)
    val end = LocalDate.parse(details.plan.weekEndDate)
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("ru"))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Итоги недели") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    start.format(formatter) + " — " + end.format(formatter),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                details.categories.forEach { category ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            category.category.categoryName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (category.habits.isNotEmpty()) {
                            Text(
                                category.habits.joinToString { it.habitName },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (category.targets.isEmpty()) {
                            Text("Цели не были заданы", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        category.targets.forEach { target ->
                            val actual = target.actualValue ?: 0
                            val percent = (actual * 100.0 / target.targetValue).roundToInt()
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    targetTypeLabel(target.targetType) + ": " +
                                        formatTarget(target.targetType, actual, target.unit) + " / " +
                                        formatTarget(target.targetType, target.targetValue, target.unit)
                                )
                                Text(
                                    percent.toString() + "% выполнения",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when {
                                        percent >= 100 -> MaterialTheme.colorScheme.primary
                                        percent >= 70 -> MaterialTheme.colorScheme.onSurface
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                )
                            }
                        }
                    }
                    HorizontalDivider()
                }

                Text(
                    "Оценка недели: " + (details.plan.loadRating?.let(::loadRatingLabel) ?: "не указана"),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                details.plan.note?.let { note ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Заметка", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(note)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        }
    )
}

private fun targetTypeLabel(type: String): String {
    return when (type.uppercase()) {
        "TIME" -> "Время"
        "CHECK" -> "Отметки"
        "COUNTER" -> "Счётчик"
        else -> type
    }
}

private fun formatTarget(type: String, value: Int, unit: String): String {
    return when (type.uppercase()) {
        "TIME" -> {
            val hours = value / 60
            val minutes = value % 60
            when {
                hours > 0 && minutes > 0 -> hours.toString() + " ч " + minutes + " мин"
                hours > 0 -> hours.toString() + " ч"
                else -> minutes.toString() + " мин"
            }
        }
        else -> value.toString() + " " + unit
    }
}

private fun loadRatingLabel(rating: String): String {
    return when (rating) {
        "STRONG_SHORTAGE" -> "Сильный недобор"
        "SHORTAGE" -> "Недобор"
        "NORMAL" -> "Нормально"
        "ABOVE_NORMAL" -> "Выше нормы"
        "EXCESSIVE" -> "Чрезмерная нагрузка"
        else -> "Оценка не указана"
    }
}
