package com.example.habittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

private val HistoryIndigo = Color(0xFF5968E8)
private val HistoryViolet = Color(0xFF8B75EE)
private val HistoryPale = Color(0xFFF0EFFF)
private val HistoryGreen = Color(0xFF25B879)

@Composable
fun WeeklyPlanHistoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: WeeklyPlanHistoryViewModel = viewModel(factory = WeeklyPlanHistoryViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()

    uiState.selectedPlan?.let { details ->
        ApprovedWeeklyPlanDialog(details = details, onDismiss = viewModel::closePlan)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FF))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = HistoryIndigo)
            }
            Spacer(Modifier.width(4.dp))
            Column {
                Text(
                    "История планов",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF20234D)
                )
                Text("Ваши завершённые недели", color = Color(0xFF777A9A), style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = HistoryIndigo)
                }
            }

            uiState.plans.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(76.dp).clip(CircleShape).background(HistoryPale),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = HistoryIndigo, modifier = Modifier.size(36.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Завершённых недель пока нет", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Утверждённые планы будут появляться здесь.",
                        color = Color(0xFF777A9A)
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        HistorySummaryCard(count = uiState.plans.size)
                    }
                    items(items = uiState.plans, key = { it.id }) { plan ->
                        WeeklyPlanHistoryCard(plan = plan, onClick = { viewModel.openPlan(plan.id) })
                    }
                    if (uiState.error != null) {
                        item { Text(uiState.error!!, color = MaterialTheme.colorScheme.error) }
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }

    if (uiState.isLoadingDetails) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text("Загрузка плана…") },
            text = { CircularProgressIndicator(color = HistoryIndigo) }
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
private fun HistorySummaryCard(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFECEBFF), Color(0xFFF5F1FF))))
            .padding(17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.History, contentDescription = null, tint = HistoryIndigo, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(13.dp))
        Column {
            Text("$count завершённых недель", fontWeight = FontWeight.Bold, color = Color(0xFF25284F))
            Text("Все утверждённые планы хранятся здесь", style = MaterialTheme.typography.bodySmall, color = Color(0xFF777A9A))
        }
    }
}

@Composable
private fun WeeklyPlanHistoryCard(plan: WeeklyPlanEntity, onClick: () -> Unit) {
    val start = LocalDate.parse(plan.weekStartDate)
    val end = LocalDate.parse(plan.weekEndDate)
    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(43.dp).clip(CircleShape).background(Color(0xFFE5F8EF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HistoryGreen, modifier = Modifier.size(25.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    start.format(formatter) + " — " + end.format(formatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF25284F)
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(
                        "Утверждён",
                        color = Color(0xFF19865A),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.clip(CircleShape).background(Color(0xFFE2F7EC)).padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                    plan.loadRating?.let { rating ->
                        Text(loadRatingLabel(rating), style = MaterialTheme.typography.bodySmall, color = Color(0xFF777A9A))
                    }
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Открыть", tint = Color(0xFF8587A4))
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
        shape = RoundedCornerShape(26.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Итоги недели", fontWeight = FontWeight.Bold)
                Text(
                    start.format(formatter) + " — " + end.format(formatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777A9A)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                details.categories.forEach { category ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(17.dp))
                            .background(Color(0xFFF7F7FD))
                            .padding(13.dp),
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        Text(category.category.categoryName, fontWeight = FontWeight.Bold, color = Color(0xFF292C55))
                        if (category.habits.isNotEmpty()) {
                            Text(
                                category.habits.joinToString { it.habitName },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8587A4)
                            )
                        }
                        if (category.targets.isEmpty()) {
                            Text("Цели не были заданы", color = Color(0xFF8587A4))
                        }
                        category.targets.forEach { target ->
                            val actual = target.actualValue ?: 0
                            val percent = (actual * 100.0 / target.targetValue).roundToInt()
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(targetTypeLabel(target.targetType), modifier = Modifier.weight(1f), color = Color(0xFF666987))
                                    Text(
                                        "${formatTarget(target.targetType, actual, target.unit)} / ${formatTarget(target.targetType, target.targetValue, target.unit)}",
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF34375F)
                                    )
                                }
                                HistoryProgressBar(percent.coerceIn(0, 100))
                                Text(
                                    "$percent% выполнения",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (percent >= 100) HistoryGreen else Color(0xFF777A9A)
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(HistoryPale).padding(13.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("Оценка недели", fontWeight = FontWeight.SemiBold, color = Color(0xFF292C55))
                    Text(
                        details.plan.loadRating?.let(::loadRatingLabel) ?: "Не указана",
                        color = HistoryIndigo
                    )
                }
                details.plan.note?.let { note ->
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF7F7FD)).padding(13.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("Заметка", fontWeight = FontWeight.SemiBold, color = Color(0xFF292C55))
                        Text(note, color = Color(0xFF555875))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        }
    )
}

@Composable
private fun HistoryProgressBar(percent: Int) {
    Box(
        modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(Color(0xFFE4E5F2))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(percent / 100f).height(7.dp)
                .clip(CircleShape).background(Brush.horizontalGradient(listOf(HistoryIndigo, HistoryGreen)))
        )
    }
}

private fun targetTypeLabel(type: String): String = when (type.uppercase()) {
    "TIME" -> "Время"
    "CHECK" -> "Отметки"
    "COUNTER" -> "Счётчик"
    else -> type
}

private fun formatTarget(type: String, value: Int, unit: String): String = when (type.uppercase()) {
    "TIME" -> {
        val hours = value / 60
        val minutes = value % 60
        when {
            hours > 0 && minutes > 0 -> "${hours} ч ${minutes} мин"
            hours > 0 -> "${hours} ч"
            else -> "${minutes} мин"
        }
    }
    else -> "$value $unit"
}

private fun loadRatingLabel(rating: String): String = when (rating) {
    "STRONG_SHORTAGE" -> "Сильный недобор"
    "SHORTAGE" -> "Недобор"
    "NORMAL" -> "Нормально"
    "ABOVE_NORMAL" -> "Выше нормы"
    "EXCESSIVE" -> "Чрезмерная нагрузка"
    else -> "Оценка не указана"
}
