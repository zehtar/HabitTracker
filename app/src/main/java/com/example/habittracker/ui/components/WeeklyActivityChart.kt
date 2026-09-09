package com.example.habittracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.DailyStatisticUiModel
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyActivityChart(
    statistics: List<DailyStatisticUiModel>,
    modifier: Modifier = Modifier
) {

    if (statistics.isEmpty()) {
        return
    }

    val surfaceColor =
        MaterialTheme.colorScheme.surface

    val lineColor =
        MaterialTheme.colorScheme.primary

    val gridColor =
        MaterialTheme.colorScheme.outlineVariant

    val textColor =
        MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(
                    horizontal = 8.dp
                )
        ) {

            val width = size.width
            val height = size.height

            val topPadding = 16f
            val bottomPadding = 32f

            val graphHeight =
                height - topPadding - bottomPadding

            val maxValue = 100f

            val pointSpacing =
                if (statistics.size > 1) {
                    width / (statistics.size - 1)
                } else {
                    width
                }

            // Горизонтальные линии
            listOf(
                0,
                25,
                50,
                75,
                100
            ).forEach { value ->

                val y =
                    topPadding +
                            graphHeight -
                            (value / maxValue) *
                            graphHeight

                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            val points =
                statistics.mapIndexed { index, statistic ->

                    val x =
                        index * pointSpacing

                    val y =
                        topPadding +
                                graphHeight -
                                (
                                        statistic.completionPercent /
                                                maxValue
                                        ) * graphHeight

                    Offset(x, y)
                }

            // Линия графика
            if (points.size > 1) {

                val path = Path()

                path.moveTo(
                    points.first().x,
                    points.first().y
                )

                for (index in 1 until points.size) {

                    val previous =
                        points[index - 1]

                    val current =
                        points[index]

                    val controlX =
                        (previous.x + current.x) / 2f

                    path.cubicTo(
                        controlX,
                        previous.y,
                        controlX,
                        current.y,
                        current.x,
                        current.y
                    )
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Точки
            points.forEach { point ->

                drawCircle(
                    color = lineColor,
                    radius = 7f,
                    center = point
                )

                drawCircle(
                    color = surfaceColor,
                    radius = 3f,
                    center = point
                )
            }
        }

        // Названия дней
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp
                )
        ) {

            statistics.forEach { statistic ->

                Text(
                    text = statistic.date
                        .dayOfWeek
                        .getDisplayName(
                            TextStyle.SHORT,
                            Locale("ru")
                        )
                        .replaceFirstChar {
                            it.uppercase()
                        },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme
                        .typography
                        .labelMedium,
                    color = textColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}