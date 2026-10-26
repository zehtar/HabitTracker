
package com.example.habittracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.HabitUiModel

@Composable
fun HabitCard(
    modifier: Modifier = Modifier,
    habit: HabitUiModel,
    completed: Boolean,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (completed) 1.015f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "CardScale"
    )

    val verticalPadding by animateDpAsState(
        targetValue = if (completed) 14.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "VerticalPadding"
    )

    val borderColor by animateColorAsState(
        targetValue = if (completed) {
            Color(habit.color)
        } else {
            Color(habit.color).copy(alpha = 0.5f)
        },
        animationSpec = tween(400),
        label = "BorderColor"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (completed) {
            Color(habit.color).copy(alpha = 0.08f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "CardColor"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = 16.dp,
                vertical = verticalPadding
            )
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = verticalPadding
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = habit.icon,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.width(8.dp))

                    CategoryPriorityIndicators(
                        category = habit.category,
                        priority = habit.priority
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = habit.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (habit.habitType.uppercase()) {
                        "TIME" -> {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Затраченное время",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.width(6.dp))

                            Text("${habit.minutesSpent} мин")
                        }

                        "COUNTER" -> {
                            Text(
                                text = "Количество: ${habit.count}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        "CHECK" -> {
                            // Для привычки с галочкой
                            // дополнительное значение не отображается.
                        }

                        else -> {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Затраченное время",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.width(6.dp))

                            Text("${habit.minutesSpent} мин")
                        }
                    }

                    StatusIndicator(
                        completed = completed,
                        color = Color(habit.color),
                        modifier = Modifier.padding(start = 24.dp)
                    )

                    Spacer(Modifier.weight(1f))
                }
            }

            IconButton(
                onClick = onMenuClick
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Меню"
                )
            }
        }
    }
}

@Composable
private fun CategoryPriorityIndicators(
    category: String?,
    priority: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ColorIndicator(
            color = categoryColor(category),
            description = "Категория"
        )

        ColorIndicator(
            color = priorityColor(priority),
            description = "Приоритет"
        )
    }
}

@Composable
private fun ColorIndicator(
    color: Color,
    description: String
) {
    Box(
        modifier = Modifier
            .size(9.dp)
            .background(
                color = color,
                shape = CircleShape
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.18f
                ),
                shape = CircleShape
            )
    )
}

private fun categoryColor(category: String?): Color {
    return when (category?.uppercase()) {
        "WORK_DEVELOPMENT" -> Color(0xFF4285F4)
        "HEALTH" -> Color(0xFF34A853)
        "LEARNING" -> Color(0xFF9C27B0)
        "HOBBY" -> Color(0xFF00A6A6)
        "HOME_EVERYDAY" -> Color(0xFF8D6E63)
        "PERSONAL_DEVELOPMENT" -> Color(0xFF29B6F6)
        else -> Color(0xFF9E9E9E)
    }
}

private fun priorityColor(priority: String): Color {
    return when (priority.uppercase()) {
        "MANDATORY" -> Color(0xFFE53935)
        "IMPORTANT" -> Color(0xFFFF9800)
        "ADDITIONAL" -> Color(0xFF9E9E9E)
        else -> Color(0xFF9E9E9E)
    }
}

@Composable
private fun StatusIndicator(
    completed: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    val statusScale by animateFloatAsState(
        targetValue = if (completed) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "StatusScale"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (completed) "⬤" else "◯",
            modifier = Modifier.graphicsLayer {
                scaleX = statusScale
                scaleY = statusScale
            },
            color = if (completed) {
                color
            } else {
                MaterialTheme.colorScheme.outline
            }
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = if (completed) {
                "Выполнено"
            } else {
                "Не выполнено"
            },
            color = if (completed) {
                color
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
