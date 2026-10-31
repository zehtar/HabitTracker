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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.habittracker.data.repository.WeeklyPlanCategoryDetails
import com.example.habittracker.data.repository.WeeklyPlanTargetInput
import com.example.habittracker.viewmodel.WeeklyPlanViewModel
import com.example.habittracker.viewmodel.WeeklyPlanViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val PlanIndigo = Color(0xFF5968E8)
private val PlanViolet = Color(0xFF8B75EE)
private val PlanPale = Color(0xFFF0EFFF)
private val PlanGreen = Color(0xFF25B879)

@Composable
fun WeeklyPlanScreen(onOpenHistory: () -> Unit) {
    val context = LocalContext.current
    val viewModel: WeeklyPlanViewModel = viewModel(factory = WeeklyPlanViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadPlan() }

    val pendingReport = uiState.pendingReport
    if (pendingReport != null && uiState.isReportVisible) {
        WeeklyPlanReportDialog(
            report = pendingReport,
            isApproving = uiState.isApproving,
            isTestReport = uiState.isTestReport,
            onContinue = if (uiState.isTestReport) viewModel::finishTestReport else viewModel::dismissReport,
            onApprove = viewModel::approveReport
        )
    }

    val monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
    val sunday = monday.plusDays(6)
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Недельный план",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF20234D)
                )
                Text(
                    "${monday.format(dateFormatter)} — ${sunday.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF777A9A)
                )
            }
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(PlanPale),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = PlanIndigo)
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
                    CircularProgressIndicator(color = PlanIndigo)
                    Spacer(Modifier.height(12.dp))
                    Text("Загружаем план недели…", color = Color(0xFF777A9A))
                }
            }

            uiState.details != null -> {
                val details = uiState.details!!
                val isDraft = details.plan.status == "DRAFT"

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        PlanHeroCard(
                            isDraft = isDraft,
                            onCheck = viewModel::finishTestWeek,
                            onHistory = onOpenHistory,
                            onPreviousReport = if (pendingReport != null) viewModel::showReportAgain else null,
                            isBusy = uiState.isApproving
                        )
                    }

                    if (details.uncategorizedHabitCount > 0) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF0F0F8))
                                    .padding(horizontal = 14.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "ⓘ",
                                    color = PlanIndigo,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Без категории: ${details.uncategorizedHabitCount}. Эти привычки пока не входят в план.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF666987)
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Цели на неделю",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF25284F),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${details.categories.size} катег.",
                                style = MaterialTheme.typography.labelMedium,
                                color = PlanIndigo,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PlanPale)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (details.categories.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(Modifier.padding(20.dp)) {
                                    Text("Пока нет категорий", fontWeight = FontWeight.Bold, color = Color(0xFF25284F))
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Назначьте категорию хотя бы одной привычке на главном экране. После этого она появится здесь.",
                                        color = Color(0xFF777A9A)
                                    )
                                }
                            }
                        }
                    } else {
                        items(details.categories, key = { it.category.id }) { category ->
                            WeeklyPlanCategoryCard(
                                category = category,
                                editable = isDraft && !uiState.isSaving,
                                onEdit = viewModel::dismissError,
                                onSave = { targets -> viewModel.saveCategoryTargets(category, targets) },
                                isSaving = uiState.isSaving
                            )
                        }
                    }

                    if (uiState.error != null) {
                        item {
                            Text(
                                uiState.error!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(uiState.error ?: "Не удалось открыть план недели", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadPlan(force = true) }) { Text("Повторить") }
                }
            }
        }
    }
}

@Composable
private fun PlanHeroCard(
    isDraft: Boolean,
    onCheck: () -> Unit,
    onHistory: () -> Unit,
    onPreviousReport: (() -> Unit)?,
    isBusy: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF6878F3), Color(0xFF9582F0))))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (isDraft) "Эта неделя" else "План на эту неделю",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (isDraft) "Время задать ориентиры и распределить нагрузку" else "План утверждён и сохранён",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE9E9FF)
                )
            }
            Box(
                modifier = Modifier.size(54.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isDraft) Icons.Default.Schedule else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            if (isDraft) {
                Button(
                    onClick = onCheck,
                    enabled = !isBusy,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = PlanIndigo
                    )
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = null)
                    Spacer(Modifier.width(7.dp))
                    Text("Проверить план", fontWeight = FontWeight.SemiBold)
                }
            }
            Button(
                onClick = onHistory,
                modifier = Modifier.weight(if (isDraft) 1f else 1f),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.18f),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(Modifier.width(7.dp))
                Text("История")
            }
        }
        if (onPreviousReport != null) {
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = onPreviousReport) {
                Text("Посмотреть итоги прошлой недели", color = Color.White)
            }
        }
    }
}

@Composable
private fun WeeklyPlanCategoryCard(
    category: WeeklyPlanCategoryDetails,
    editable: Boolean,
    onEdit: () -> Unit,
    onSave: (List<WeeklyPlanTargetInput>) -> Unit,
    isSaving: Boolean
) {
    var showEditor by remember(category.category.id) { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryGlyph(category.category.categoryName)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        category.category.categoryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF25284F)
                    )
                    Text(
                        "${category.habits.size} привычек",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8587A4)
                    )
                }
                if (editable) {
                    TextButton(onClick = {
                        onEdit()
                        showEditor = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Изменить")
                    }
                }
            }

            Text(
                category.habits.joinToString { it.habitName },
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF777A9A)
            )

            if (category.targets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp))
                        .background(Color(0xFFF5F5FB)).padding(12.dp)
                ) {
                    Text("Цели на неделю пока не заданы", color = Color(0xFF8587A4), style = MaterialTheme.typography.bodySmall)
                }
            } else {
                category.targets.forEach { target ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(13.dp))
                            .background(Color(0xFFF7F7FD))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            targetTypeLabel(target.targetType),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF626584),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            formatTarget(target.targetType, target.targetValue, target.unit),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PlanIndigo
                        )
                    }
                }
            }
        }
    }

    if (showEditor) {
        WeeklyPlanTargetDialog(
            category = category,
            isSaving = isSaving,
            onDismiss = { showEditor = false },
            onSave = {
                onSave(it)
                showEditor = false
            }
        )
    }
}

@Composable
private fun CategoryGlyph(name: String) {
    val normalized = name.lowercase()
    val glyph = when {
        "здоров" in normalized -> "♥"
        "работ" in normalized || "развити" in normalized -> "▣"
        "обуч" in normalized || "учёб" in normalized || "учеб" in normalized -> "✦"
        "хобби" in normalized -> "✿"
        "дом" in normalized -> "⌂"
        else -> "●"
    }
    val tint = when {
        "здоров" in normalized -> Color(0xFFE96B91)
        "работ" in normalized || "развити" in normalized -> Color(0xFF5378E8)
        "обуч" in normalized || "учёб" in normalized || "учеб" in normalized -> Color(0xFF8A70D8)
        else -> Color(0xFF5A9E9A)
    }
    Box(
        modifier = Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Text(glyph, color = tint, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WeeklyPlanTargetDialog(
    category: WeeklyPlanCategoryDetails,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (List<WeeklyPlanTargetInput>) -> Unit
) {
    val availableTypes = remember(category.category.id) {
        category.habits.map { it.habitType.uppercase() }.distinct().sorted()
    }
    val values = remember(category.category.id) {
        mutableStateMapOf<String, String>().apply {
            category.targets.forEach { target ->
                val initialValue = if (target.targetType.equals("TIME", true)) {
                    (target.targetValue / 60.0).toString()
                } else target.targetValue.toString()
                put(target.targetType.uppercase(), initialValue)
            }
        }
    }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(26.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Настройка целей", fontWeight = FontWeight.Bold)
                Text(
                    category.category.categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PlanIndigo
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Text(
                    "Задайте ориентиры на всю неделю. Поле можно оставить пустым, если этот тип цели не нужен.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777A9A)
                )
                availableTypes.forEach { type ->
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(targetTypeLabel(type), fontWeight = FontWeight.SemiBold, color = Color(0xFF34375F))
                        OutlinedTextField(
                            value = values[type].orEmpty(),
                            onValueChange = {
                                values[type] = it
                                validationError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(15.dp),
                            label = {
                                Text(
                                    when (type) {
                                        "TIME" -> "Часов за неделю"
                                        "CHECK" -> "Количество отметок"
                                        else -> "Общее количество"
                                    }
                                )
                            },
                            suffix = {
                                Text(when (type) {
                                    "TIME" -> "ч"
                                    "CHECK" -> "раз"
                                    else -> "ед."
                                })
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
                if (validationError != null) {
                    Text(validationError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving,
                shape = RoundedCornerShape(13.dp),
                onClick = {
                    val targets = mutableListOf<WeeklyPlanTargetInput>()
                    for (type in availableTypes) {
                        val rawValue = values[type].orEmpty().trim().replace(',', '.')
                        if (rawValue.isBlank()) continue
                        val numericValue = rawValue.toDoubleOrNull()
                        if (numericValue == null || numericValue <= 0.0) {
                            validationError = "Введите положительное число для «${targetTypeLabel(type).lowercase()}»."
                            return@Button
                        }
                        val storedValue = if (type == "TIME") (numericValue * 60.0).roundToInt() else numericValue.roundToInt()
                        if (storedValue <= 0) {
                            validationError = "Значение слишком мало."
                            return@Button
                        }
                        targets += WeeklyPlanTargetInput(
                            targetType = type,
                            targetValue = storedValue,
                            unit = if (type == "TIME") "мин" else if (type == "CHECK") "раз" else "ед."
                        )
                    }
                    onSave(targets)
                }
            ) {
                Text(if (isSaving) "Сохранение…" else "Сохранить цели")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Отмена") }
        }
    )
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

@Composable
private fun WeeklyPlanReportDialog(
    report: com.example.habittracker.data.repository.WeeklyPlanDetails,
    isApproving: Boolean,
    isTestReport: Boolean,
    onContinue: () -> Unit,
    onApprove: (String?, String) -> Unit
) {
    var note by remember(report.plan.id) { mutableStateOf(report.plan.note.orEmpty()) }
    var selectedRating by remember(report.plan.id) { mutableStateOf(report.plan.loadRating ?: "NORMAL") }
    val start = LocalDate.parse(report.plan.weekStartDate)
    val end = LocalDate.parse(report.plan.weekEndDate)
    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))

    val targetCount = report.categories.sumOf { it.targets.size }
    val completedCount = report.categories.sumOf { category ->
        category.targets.count { (it.actualValue ?: 0) >= it.targetValue }
    }
    val overallPercent = if (targetCount == 0) 0 else (completedCount * 100.0 / targetCount).roundToInt()

    AlertDialog(
        onDismissRequest = { if (!isApproving) onContinue() },
        shape = RoundedCornerShape(26.dp),
        title = {
            Text(
                if (isTestReport) "Прогресс текущей недели" else "Итоги прошлой недели",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Text("${start.format(formatter)} — ${end.format(formatter)}", color = Color(0xFF777A9A))
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PlanPale)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(58.dp).clip(CircleShape).background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$overallPercent%", color = PlanIndigo, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Общий прогресс", fontWeight = FontWeight.Bold, color = Color(0xFF25284F))
                            Text(
                                if (targetCount == 0) "Цели пока не заданы" else "$completedCount из $targetCount целей выполнено",
                                color = Color(0xFF777A9A),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                if (report.categories.isEmpty()) {
                    Text("На этой неделе не было категорийных планов.")
                }

                report.categories.forEach { category ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(17.dp))
                            .background(Color(0xFFF7F7FD))
                            .padding(13.dp),
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        Text(category.category.categoryName, fontWeight = FontWeight.Bold, color = Color(0xFF292C55))
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
                                PlanProgressBar(percent.coerceIn(0, 100))
                                Text(
                                    "$percent% выполнения",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (percent >= 100) PlanGreen else Color(0xFF777A9A)
                                )
                            }
                        }
                    }
                }

                if (!isTestReport) {
                    Text("Как прошла неделя?", fontWeight = FontWeight.SemiBold)
                    val ratings = listOf(
                        "STRONG_SHORTAGE" to "Сильный недобор",
                        "SHORTAGE" to "Недобор",
                        "NORMAL" to "Нормально",
                        "ABOVE_NORMAL" to "Выше нормы",
                        "EXCESSIVE" to "Чрезмерная нагрузка"
                    )
                    ratings.forEach { (value, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                .background(if (selectedRating == value) PlanPale else Color.Transparent)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = selectedRating == value,
                                onClick = { selectedRating = value },
                                enabled = !isApproving
                            )
                            Text(label, modifier = Modifier.weight(1f).padding(start = 4.dp))
                        }
                    }
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        label = { Text("Заметка за неделю (необязательно)") },
                        minLines = 2,
                        maxLines = 4,
                        enabled = !isApproving
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isApproving,
                shape = RoundedCornerShape(13.dp),
                onClick = { if (isTestReport) onContinue() else onApprove(note, selectedRating) }
            ) {
                Text(
                    when {
                        isApproving -> "Обработка…"
                        isTestReport -> "Закрыть"
                        else -> "Утвердить и составить новый план"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onContinue, enabled = !isApproving) {
                Text(if (isTestReport) "Закрыть" else "Продолжить позже")
            }
        }
    )
}

@Composable
private fun PlanProgressBar(percent: Int) {
    Box(
        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(Color(0xFFE4E5F2))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(percent / 100f).height(8.dp)
                .clip(CircleShape).background(Brush.horizontalGradient(listOf(PlanIndigo, PlanGreen)))
        )
    }
}
