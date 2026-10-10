
package com.example.habittracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class ActivityTrendMode {
    COMPLETION,
    TIME
}

data class ActivityTrendPoint(
    val label: String,
    val completionPercent: Int,
    val minutes: Int
)

@Composable
fun ActivityTrendChart(
    points: List<ActivityTrendPoint>,
    mode: ActivityTrendMode,
    onModeChange: (ActivityTrendMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = mode == ActivityTrendMode.COMPLETION,
                onClick = {
                    onModeChange(ActivityTrendMode.COMPLETION)
                },
                label = { Text("Выполнение") },
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = mode == ActivityTrendMode.TIME,
                onClick = {
                    onModeChange(ActivityTrendMode.TIME)
                },
                label = { Text("Время") },
                modifier = Modifier.weight(1f)
            )
        }

        if (points.isEmpty()) {
            Text(
                text = "Нет данных за выбранный период",
                modifier = Modifier.padding(vertical = 24.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        val primaryColor = MaterialTheme.colorScheme.primary
        val gridColor = MaterialTheme.colorScheme.outlineVariant
        val pointColor = MaterialTheme.colorScheme.surface

        val values = points.map { point ->
            when (mode) {
                ActivityTrendMode.COMPLETION ->
                    point.completionPercent.coerceIn(0, 100).toFloat()

                ActivityTrendMode.TIME ->
                    point.minutes.coerceAtLeast(0).toFloat()
            }
        }

        val maxValue = when (mode) {
            ActivityTrendMode.COMPLETION -> 100f

            ActivityTrendMode.TIME -> {
                val maximum = values.maxOrNull() ?: 0f
                if (maximum <= 0f) 60f else maximum
            }
        }

        val axisLabels = when (mode) {
            ActivityTrendMode.COMPLETION ->
                listOf("0%", "25%", "50%", "75%", "100%")

            ActivityTrendMode.TIME ->
                listOf(
                    "0",
                    "${(maxValue * 0.25f).toInt()}",
                    "${(maxValue * 0.5f).toInt()}",
                    "${(maxValue * 0.75f).toInt()}",
                    maxValue.toInt().toString()
                )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .height(180.dp)
                    .padding(
                        top = 12.dp,
                        bottom = 12.dp,
                        end = 8.dp
                    ),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                axisLabels.reversed().forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
            ) {
                val topPadding = 12.dp.toPx()
                val bottomPadding = 12.dp.toPx()
                val graphHeight = size.height - topPadding - bottomPadding

                val pointSpacing = if (points.size > 1) {
                    size.width / (points.size - 1)
                } else {
                    size.width / 2f
                }

                for (step in 0..4) {
                    val y = topPadding + graphHeight * step / 4f

                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val graphPoints = values.mapIndexed { index, value ->
                    val x = if (points.size > 1) {
                        index * pointSpacing
                    } else {
                        size.width / 2f
                    }

                    val normalizedValue =
                        (value / maxValue).coerceIn(0f, 1f)

                    val y = topPadding +
                            graphHeight * (1f - normalizedValue)

                    Offset(x, y)
                }

                if (graphPoints.size > 1) {
                    val path = Path().apply {
                        moveTo(
                            graphPoints.first().x,
                            graphPoints.first().y
                        )

                        for (index in 1 until graphPoints.size) {
                            lineTo(
                                graphPoints[index].x,
                                graphPoints[index].y
                            )
                        }
                    }

                    drawPath(
                        path = path,
                        color = primaryColor,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                graphPoints.forEach { point ->
                    drawCircle(
                        color = primaryColor,
                        radius = 5.dp.toPx(),
                        center = point
                    )

                    drawCircle(
                        color = pointColor,
                        radius = 2.dp.toPx(),
                        center = point
                    )
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 36.dp,
                    end = 4.dp,
                    top = 8.dp
                )
                .height(24.dp)
        ) {
            val labelIndexes = when {
                points.size <= 7 -> points.indices.toList()

                else -> listOf(
                    0,
                    points.size / 4,
                    points.size / 2,
                    points.size * 3 / 4,
                    points.lastIndex
                ).distinct()
            }

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val chartWidth = maxWidth

                labelIndexes.forEach { index ->
                    val fraction = if (points.size > 1) {
                        index.toFloat() / (points.size - 1)
                    } else {
                        0.5f
                    }

                    Text(
                        text = points[index].label,
                        modifier = Modifier.offset(
                            x = chartWidth * fraction -
                                    2.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
