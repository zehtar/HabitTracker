package com.example.habittracker.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.habittracker.data.database.DatabaseProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth

class HabitWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {

        val habitsFlow =
            getTodayHabits(context)

        val WidgetTextColor = ColorProvider(
            day = Color(0xFF202124),
            night = Color(0xFFFFFFFF)
        )

        val WidgetSecondaryTextColor = ColorProvider(
            day = Color(0xFF5F6368),
            night = Color(0xFFBDBDBD)
        )
        provideContent {

            val habits by habitsFlow.collectAsState(
                initial = emptyList()
            )

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(
                        ColorProvider(
                            day = Color(0xFFFFFFFF),
                            night = Color(0xFF202124)
                        )
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text = "Habit Tracker",
                    style = TextStyle(
                        color = WidgetTextColor
                    )
                )

                Spacer(
                    modifier = GlanceModifier.height(2.dp)
                )

                Text(
                    text = "Сегодня",
                    style = TextStyle(
                        color = WidgetSecondaryTextColor
                    )
                )

                Spacer(
                    modifier = GlanceModifier.height(12.dp)
                )

                habits.forEach { habit ->

                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 5.dp
                            )
                    ) {

                        Text(
                            text = "${habit.icon} ${habit.name}",
                            modifier = GlanceModifier.defaultWeight(),
                            style = TextStyle(
                                color = WidgetTextColor
                            )
                        )

                        Text(
                            text =
                                if (habit.completed)
                                    "✓"
                                else
                                    "○",

                            style = TextStyle(
                                color = if (habit.completed) {
                                    ColorProvider(
                                        day = Color(habit.color),
                                        night = Color(habit.color)
                                    )
                                } else {
                                    WidgetSecondaryTextColor
                                }
                            )
                        )
                    }
                }

                Spacer(
                    modifier = GlanceModifier.height(8.dp)
                )

                Text(
                    text =
                        "Выполнено: " +
                                "${habits.count { it.completed }} / ${habits.size}",

                    style = TextStyle(
                        color = WidgetSecondaryTextColor
                    )
                )
            }
        }
    }
}
private fun getTodayHabits(
    context: Context
): Flow<List<HabitWidgetItem>> {

    val database =
        DatabaseProvider.getDatabase(context)

    val today =
        LocalDate.now().toString()

    return combine(
        database.habitDao().getAllHabits(),
        database.habitEntryDao().getEntriesForDate(today)
    ) { habits, entries ->

        habits.map { habit ->

            val entry =
                entries.find {
                    it.habitId == habit.id
                }

            HabitWidgetItem(
                id = habit.id,
                name = habit.name,
                icon = habit.icon,
                completed = entry?.completed ?: false,
                minutesSpent = entry?.minutesSpent ?: 0,
                color = habit.color
            )
        }
    }
}