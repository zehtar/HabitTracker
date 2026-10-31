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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

@Composable
fun WeeklyPlanScreen() {
    val context = LocalContext.current
    val viewModel: WeeklyPlanViewModel = viewModel(
        factory = WeeklyPlanViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPlan()
    }

    val pendingReport = uiState.pendingReport
    if (pendingReport != null && uiState.isReportVisible) {
        WeeklyPlanReportDialog(
            report = pendingReport,
            isApproving = uiState.isApproving,
            onContinue = viewModel::dismissReport,
            onApprove = viewModel::approveReport
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Планирование",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))

        val monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))

        Text(
            text = "Неделя: ${monday.format(dateFormatter)} — ${sunday.format(dateFormatter)}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Загружаем план недели…")
                }
            }

            uiState.details != null -> {
                val details = uiState.details!!
                val isDraft = details.plan.status == "DRAFT"

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (pendingReport != null) {
                        item {
                            OutlinedButton(
                                onClick = viewModel::showReportAgain,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Итоги прошлой недели")
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isDraft) "Черновик плана" else "План утверждён",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = if (isDraft) {
                                        "Добавьте недельные ориентиры для категорий привычек."
                                    } else {
                                        "Этот план сохранён и доступен только для просмотра."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (details.uncategorizedHabitCount > 0) {
                        item {
                            Text(
                                text = "Привычек без категории: ${details.uncategorizedHabitCount}. Они не участвуют в планировании.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    if (details.categories.isEmpty()) {
                        item {
                            Card {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        "Пока нет привычек с категориями",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Назначьте категорию хотя бы одной привычке на главном экране. После этого она появится здесь.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = details.categories,
                            key = { it.category.id }
                        ) { category ->
                            WeeklyPlanCategoryCard(
                                category = category,
                                editable = isDraft && !uiState.isSaving,
                                onEdit = {
                                    viewModel.dismissError()
                                    // The dialog is owned by the card.
                                },
                                onSave = { targets ->
                                    viewModel.saveCategoryTargets(category, targets)
                                },
                                isSaving = uiState.isSaving
                            )
                        }
                    }

                    if (uiState.error != null) {
                        item {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    item {
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error ?: "Не удалось открыть план недели",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadPlan(force = true) }) {
                        Text("Повторить")
                    }
                }
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
    var showEditor by remember(category.category.id) {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.category.categoryName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (editable) {
                    TextButton(
                        onClick = {
                            onEdit()
                            showEditor = true
                        }
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                        Text("Изменить")
                    }
                }
            }

            Text(
                text = category.habits.joinToString { it.habitName },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (category.targets.isEmpty()) {
                Text(
                    text = "Цели на неделю пока не заданы",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                category.targets.forEach { target ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = targetTypeLabel(target.targetType),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = formatTarget(target.targetType, target.targetValue, target.unit),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
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
private fun WeeklyPlanTargetDialog(
    category: WeeklyPlanCategoryDetails,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (List<WeeklyPlanTargetInput>) -> Unit
) {
    val availableTypes = remember(category.category.id) {
        category.habits.map { it.habitType.uppercase() }
            .distinct()
            .sorted()
    }

    val values = remember(category.category.id) {
        mutableStateMapOf<String, String>().apply {
            category.targets.forEach { target ->
                val initialValue = if (target.targetType.equals("TIME", true)) {
                    (target.targetValue / 60.0).toString()
                } else {
                    target.targetValue.toString()
                }
                put(target.targetType.uppercase(), initialValue)
            }
        }
    }

    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Цели: ${category.category.categoryName}")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Укажите недельные цели. Пустое поле означает, что цель этого типа не задаётся.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                availableTypes.forEach { type ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = targetTypeLabel(type),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        OutlinedTextField(
                            value = values[type].orEmpty(),
                            onValueChange = {
                                values[type] = it
                                validationError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
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
                                Text(
                                    when (type) {
                                        "TIME" -> "ч"
                                        "CHECK" -> "раз"
                                        else -> "ед."
                                    }
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )
                    }
                }

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving,
                onClick = {
                    val targets = mutableListOf<WeeklyPlanTargetInput>()

                    for (type in availableTypes) {
                        val rawValue = values[type].orEmpty()
                            .trim()
                            .replace(',', '.')

                        if (rawValue.isBlank()) continue

                        val numericValue = rawValue.toDoubleOrNull()
                        if (numericValue == null || numericValue <= 0.0) {
                            validationError = "Введите положительное число для «${targetTypeLabel(type).lowercase()}»."
                            return@Button
                        }

                        val storedValue = if (type == "TIME") {
                            (numericValue * 60.0).roundToInt()
                        } else {
                            numericValue.roundToInt()
                        }

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
                Text(if (isSaving) "Сохранение…" else "Сохранить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("Отмена")
            }
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

private fun formatTarget(
    type: String,
    value: Int,
    unit: String
): String {
    return when (type.uppercase()) {
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
}


@Composable
private fun WeeklyPlanReportDialog(
    report: com.example.habittracker.data.repository.WeeklyPlanDetails,
    isApproving: Boolean,
    onContinue: () -> Unit,
    onApprove: (String?, String) -> Unit
) {
    var note by remember(report.plan.id) {
        mutableStateOf(report.plan.note.orEmpty())
    }
    var selectedRating by remember(report.plan.id) {
        mutableStateOf(report.plan.loadRating ?: "NORMAL")
    }

    val start = LocalDate.parse(report.plan.weekStartDate)
    val end = LocalDate.parse(report.plan.weekEndDate)
    val formatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))

    AlertDialog(
        onDismissRequest = {
            if (!isApproving) onContinue()
        },
        title = { Text("Итоги прошлой недели") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "${start.format(formatter)} — ${end.format(formatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (report.categories.isEmpty()) {
                    Text("На прошлой неделе не было категорийных планов.")
                }

                report.categories.forEach { category ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            category.category.categoryName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (category.targets.isEmpty()) {
                            Text(
                                "Цели не были заданы",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        category.targets.forEach { target ->
                            val actual = target.actualValue ?: 0
                            val percent = (actual * 100.0 / target.targetValue).roundToInt()
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    "${targetTypeLabel(target.targetType)}: " +
                                        "${formatTarget(target.targetType, actual, target.unit)} / " +
                                        formatTarget(target.targetType, target.targetValue, target.unit)
                                )
                                Text(
                                    "$percent% выполнения",
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
                    "Как прошла неделя?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                val ratings = listOf(
                    "STRONG_SHORTAGE" to "Сильный недобор",
                    "SHORTAGE" to "Недобор",
                    "NORMAL" to "Нормально",
                    "ABOVE_NORMAL" to "Выше нормы",
                    "EXCESSIVE" to "Чрезмерная нагрузка"
                )
                ratings.forEach { (value, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedRating == value,
                            onClick = { selectedRating = value },
                            enabled = !isApproving
                        )
                        Text(
                            text = label,
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Заметка за неделю (необязательно)") },
                    minLines = 2,
                    maxLines = 4,
                    enabled = !isApproving
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isApproving,
                onClick = { onApprove(note, selectedRating) }
            ) {
                Text(if (isApproving) "Сохранение…" else "Утвердить и составить новый план")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onContinue,
                enabled = !isApproving
            ) {
                Text("Продолжить позже")
            }
        }
    )
}
