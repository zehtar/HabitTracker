package com.example.habittracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.habittracker.ui.model.DailyStatisticUiModel
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyTimeChart(
    statistics: List<DailyStatisticUiModel>,
    modifier: Modifier = Modifier
) {

    if (statistics.isEmpty()) {
        return
    }

    val lineColor =
        MaterialTheme.colorScheme.primary

    val gridColor =
        MaterialTheme.colorScheme.outlineVariant

    val textColor =
        MaterialTheme.colorScheme.onSurfaceVariant

    val surfaceColor =
        MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
    ) {

        val maxMinutes =
            maxOf(
                statistics.maxOf { it.minutes },
                60
            )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 8.dp)
        ) {

            val width = size.width
            val height = size.height

            val topPadding = 16f
            val bottomPadding = 32f

            val graphHeight =
                height - topPadding - bottomPadding

            val pointSpacing =
                if (statistics.size > 1) {
                    width / (statistics.size - 1)
                } else {
                    width
                }

            val gridValues = listOf(
                0,
                maxMinutes / 4,
                maxMinutes / 2,
                maxMinutes * 3 / 4,
                maxMinutes
            )

            gridValues.forEach { value ->

                val y =
                    topPadding +
                            graphHeight -
                            (value.toFloat() / maxMinutes) *
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
                                        statistic.minutes
                                            .toFloat() /
                                                maxMinutes
                                        ) * graphHeight

                    Offset(x, y)
                }

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
                    style = androidx.compose.ui.graphics
                        .drawscope.Stroke(
                            width = 5f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                )
            }

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
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

                    textAlign = TextAlign.Center
                )
            }
        }
    }
}