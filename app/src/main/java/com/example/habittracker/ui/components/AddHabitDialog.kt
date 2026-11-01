package com.example.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.habittracker.ui.model.HabitUiModel

private data class HabitEmojiGroup(
    val title: String,
    val emojis: List<String>
)

private val habitEmojiGroups = listOf(
    HabitEmojiGroup("Все", listOf(
        "🎮", "🎯", "🎨", "🎭", "🎬", "🎧", "🎵", "🎹", "🎸", "🎤", "📷", "📚",
        "📖", "✍️", "📝", "💻", "⌨️", "🖥️", "📱", "🧠", "💡", "🔬", "🔭", "🧪",
        "💪", "🏋️", "🏃", "🚴", "🧘", "🧗", "⚽", "🏀", "🎾", "🏊", "🥊", "🛹",
        "🥗", "🍎", "🥑", "💧", "☕", "🍵", "🥛", "🛌", "🌙", "☀️", "🌿", "🪴",
        "🇯🇵", "🗣️", "🌍", "✈️", "🧳", "🏠", "🧹", "🧺", "🛠️", "🔧", "💰", "📈",
        "❤️", "🤝", "👨‍👩‍👧", "🐶", "🐱", "🐾", "⭐", "🔥", "🏆", "🚀", "⏰", "✅"
    )),
    HabitEmojiGroup("Работа", listOf("💻", "⌨️", "🖥️", "📱", "📝", "📊", "📈", "💼", "💡", "🛠️", "🔧", "🚀")),
    HabitEmojiGroup("Обучение", listOf("📚", "📖", "✍️", "🧠", "🔬", "🔭", "🧪", "🇯🇵", "🗣️", "🎓", "💬", "📝")),
    HabitEmojiGroup("Здоровье", listOf("💪", "🏋️", "🏃", "🚴", "🧘", "🧗", "⚽", "🏀", "🏊", "🥊", "🥗", "🍎", "💧", "🛌", "🌙")),
    HabitEmojiGroup("Хобби", listOf("🎮", "🎯", "🎨", "🎭", "🎬", "🎧", "🎵", "🎹", "🎸", "🎤", "📷", "🛹")),
    HabitEmojiGroup("Быт", listOf("🏠", "🧹", "🧺", "🪴", "🌿", "☕", "🍵", "🥛", "🐶", "🐱", "🐾", "🛒"))
)

private val habitColorPalette = listOf(
    0xFF2196F3, 0xFF1976D2, 0xFF03A9F4, 0xFF00BCD4, 0xFF009688, 0xFF4CAF50,
    0xFF8BC34A, 0xFFCDDC39, 0xFFFFC107, 0xFFFF9800, 0xFFFF5722, 0xFFF44336,
    0xFFE91E63, 0xFF9C27B0, 0xFF673AB7, 0xFF795548, 0xFF607D8B, 0xFF9E9E9E
)

@Composable
fun AddHabitDialog(
    habit: HabitUiModel? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        String,
        String,
        Long,
        String,
        String?,
        String?,
        String
    ) -> Unit,
    onDelete: () -> Unit = {}
) {
    var name by remember(habit?.id) { mutableStateOf(habit?.name ?: "") }
    var selectedIcon by remember(habit?.id) { mutableStateOf(habit?.icon ?: "🎮") }
    var selectedColor by remember(habit?.id) {
        mutableLongStateOf(habit?.color ?: 0xFF2196F3)
    }
    var selectedType by remember(habit?.id) { mutableStateOf(habit?.habitType ?: "TIME") }
    var guideline by remember(habit?.id) { mutableStateOf(habit?.guideline.orEmpty()) }
    var selectedCategory by remember(habit?.id) { mutableStateOf(habit?.category) }
    var selectedPriority by remember(habit?.id) {
        mutableStateOf(habit?.priority ?: "IMPORTANT")
    }

    var showEmojiPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val categories = listOf(
        null to "Без категории",
        "WORK_DEVELOPMENT" to "Работа и развитие",
        "HEALTH" to "Здоровье",
        "LEARNING" to "Обучение",
        "HOBBY" to "Хобби",
        "HOME_EVERYDAY" to "Быт",
        "PERSONAL_DEVELOPMENT" to "Личное развитие"
    )
    val priorities = listOf(
        "MANDATORY" to "Обязательная",
        "IMPORTANT" to "Важная",
        "ADDITIONAL" to "Дополнительная"
    )
    val types = listOf(
        Triple("CHECK", "Выполнение", "Отмечать факт выполнения"),
        Triple("TIME", "Время", "Записывать минуты"),
        Triple("COUNTER", "Количество", "Записывать число повторений")
    )

    val habitColor = Color(selectedColor)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 720.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (habit == null) "Новая привычка" else "Редактирование",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Настройте привычку под себя",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(habitColor.copy(alpha = 0.12f))
                        .border(1.dp, habitColor.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
                        .clickable { showEmojiPicker = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(habitColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedIcon, style = MaterialTheme.typography.headlineLarge)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name.ifBlank { "Название привычки" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Нажмите, чтобы выбрать эмодзи",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Default.Edit, contentDescription = "Выбрать эмодзи", tint = habitColor)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("Тип привычки")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        types.forEach { (type, label, description) ->
                            val selected = selectedType == type
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (selected) habitColor.copy(alpha = 0.12f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                    )
                                    .border(
                                        if (selected) 1.5.dp else 1.dp,
                                        if (selected) habitColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedType = type }
                                    .padding(horizontal = 8.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = when (type) {
                                        "CHECK" -> Icons.Default.CheckCircle
                                        "TIME" -> Icons.Default.Schedule
                                        else -> Icons.Default.Tag
                                    },
                                    contentDescription = null,
                                    tint = if (selected) habitColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    minLines = 2
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle("Организация")
                    SelectionDropdown(
                        title = "Категория",
                        selectedLabel = categories.firstOrNull { it.first == selectedCategory }?.second
                            ?: "Без категории",
                        options = categories,
                        onSelected = { selectedCategory = it },
                        accent = habitColor
                    )
                    SelectionDropdown(
                        title = "Приоритет",
                        selectedLabel = priorities.first { it.first == selectedPriority }.second,
                        options = priorities,
                        onSelected = { selectedPriority = it },
                        accent = habitColor
                    )
                }

                OutlinedTextField(
                    value = guideline,
                    onValueChange = { guideline = it },
                    label = { Text("Мой ориентир") },
                    placeholder = { Text("Например: читать 20 минут") },
                    supportingText = { Text("Не влияет на статистику и серии") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        if (guideline.isNotEmpty()) {
                            TextButton(onClick = { guideline = "" }) {
                                Text("Очистить")
                            }
                        }
                    }
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionTitle("Цвет")
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { showColorPicker = true }) {
                            Text("Настроить")
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        habitColorPalette.forEach { color ->
                            val selected = color.toLong() == selectedColor
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(Color(color))
                                    .then(
                                        if (selected) Modifier.border(
                                            3.dp,
                                            MaterialTheme.colorScheme.onSurface,
                                            CircleShape
                                        ) else Modifier
                                    )
                                    .clickable { selectedColor = color.toLong() }
                            )
                        }
                    }
                    Text(
                        text = "Цвет используется для оформления карточки и индикаторов привычки",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (habit != null) {
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Удалить") }
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    Button(
                        onClick = {
                            val normalizedName = name.trim()
                            if (normalizedName.isNotEmpty()) {
                                onConfirm(
                                    normalizedName,
                                    selectedIcon,
                                    selectedColor,
                                    selectedType,
                                    guideline.trim().ifEmpty { null },
                                    selectedCategory,
                                    selectedPriority
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = habitColor)
                    ) {
                        Text(if (habit == null) "Создать" else "Сохранить")
                    }
                }
            }
        }
    }

    if (showEmojiPicker) {
        EmojiPickerDialog(
            selectedEmoji = selectedIcon,
            onDismiss = { showEmojiPicker = false },
            onSelect = {
                selectedIcon = it
                showEmojiPicker = false
            }
        )
    }

    if (showColorPicker) {
        HabitColorPickerDialog(
            selectedColor = selectedColor,
            onDismiss = { showColorPicker = false },
            onSelect = {
                selectedColor = it
                showColorPicker = false
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun <T> SelectionDropdown(
    title: String,
    selectedLabel: String,
    options: List<Pair<T, String>>,
    onSelected: (T) -> Unit,
    accent: Color
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = selectedLabel, style = MaterialTheme.typography.bodyLarge)
                }
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Выбрать")
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    },
                    trailingIcon = if (label == selectedLabel) {
                        { Icon(Icons.Default.Check, contentDescription = null, tint = accent) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun EmojiPickerDialog(
    selectedEmoji: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var selectedGroup by remember { mutableStateOf("Все") }
    var search by remember { mutableStateOf("") }
    val group = habitEmojiGroups.first { it.title == selectedGroup }
    val emojis = if (search.isBlank()) group.emojis.distinct()
    else habitEmojiGroups.flatMap { it.emojis }.distinct().filter { it.contains(search) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Выбор эмодзи", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Поиск по эмодзи") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                if (search.isBlank()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(habitEmojiGroups) { item ->
                            FilterChip(
                                selected = selectedGroup == item.title,
                                onClick = { selectedGroup = item.title },
                                label = { Text(item.title) }
                            )
                        }
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(emojis) { emoji ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (emoji == selectedEmoji) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                )
                                .clickable { onSelect(emoji) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Закрыть") }
            }
        }
    }
}

@Composable
private fun HabitColorPickerDialog(
    selectedColor: Long,
    onDismiss: () -> Unit,
    onSelect: (Long) -> Unit
) {
    var previewColor by remember(selectedColor) { mutableStateOf(Color(selectedColor)) }
    var hue by remember(selectedColor) {
        mutableFloatStateOf(
            val hsv = FloatArray(3)
            android.graphics.Color.colorToHSV(Color(selectedColor).toArgb(), hsv)
            hsv[0]
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Выбор цвета", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(previewColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Предпросмотр",
                        color = if (previewColor luminanceForText() > 0.45f) Color.Black else Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text("Быстрые цвета", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(habitColorPalette) { color ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(Color(color))
                                .border(
                                    if (Color(color).toArgb() == previewColor.toArgb()) 3.dp else 0.dp,
                                    MaterialTheme.colorScheme.onSurface,
                                    CircleShape
                                )
                                .clickable {
                                    previewColor = Color(color)
                                    val hsv = FloatArray(3)
                                    android.graphics.Color.colorToHSV(color, hsv)
                                    hue = hsv[0]
                                }
                        )
                    }
                }
                Text("Произвольный оттенок", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Slider(
                    value = hue,
                    onValueChange = {
                        hue = it
                        previewColor = Color.hsv(it, 0.78f, 0.95f)
                    },
                    valueRange = 0f..360f
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    Button(onClick = { onSelect(previewColor.toArgb().toLong() and 0xFFFFFFFFL) }) {
                        Text("Выбрать")
                    }
                }
            }
        }
    }
}

private fun Color.luminanceForText(): Float {
    return (0.2126f * red + 0.7152f * green + 0.0722f * blue)
}
