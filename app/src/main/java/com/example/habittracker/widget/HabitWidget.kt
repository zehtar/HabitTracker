package com.example.habittracker.widget
import androidx.glance.LocalSize
import androidx.glance.appwidget.SizeMode
import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.glance.appwidget.cornerRadius
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.action.actionRunCallback
import com.example.habittracker.data.database.DatabaseProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.action.actionStartActivity
import android.content.Intent
import com.example.habittracker.MainActivity
import androidx.glance.action.actionParametersOf
import androidx.glance.action.ActionParameters
class HabitWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {

        val habitsFlow = getTodayHabits(context)

        provideContent {

            val habits by habitsFlow.collectAsState(
                initial = emptyList()
            )

            val completedCount =
                habits.count { it.completed }

            val totalCount =
                habits.size

            val today = LocalDate.now()

            val dayFormatter =
                DateTimeFormatter.ofPattern(
                    "EEEE",
                    Locale("ru")
                )

            val dateFormatter =
                DateTimeFormatter.ofPattern(
                    "d MMMM yyyy",
                    Locale("ru")
                )

            val dayName =
                today
                    .format(dayFormatter)
                    .replaceFirstChar {
                        it.uppercase()
                    }

            val date =
                today.format(dateFormatter)

            WidgetContent(
                habits = habits,
                dayName = dayName,
                date = date,
                completedCount = completedCount,
                totalCount = totalCount
            )
        }
    }
}


/* ---------------------------------------------------------
   Основной UI
   --------------------------------------------------------- */

@androidx.compose.runtime.Composable
private fun WidgetContent(
    habits: List<HabitWidgetItem>,
    dayName: String,
    date: String,
    completedCount: Int,
    totalCount: Int
) {

    val background = ColorProvider(
        day = Color(0xFF111722),
        night = Color(0xFF111722)
    )

    val widgetSize = LocalSize.current

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(background)
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = GlanceModifier.defaultWeight()
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "▣",
                        style = TextStyle(
                            color = ColorProvider(
                                day = Color(0xFFB7C1D4),
                                night = Color(0xFFB7C1D4)
                            ),
                            fontSize = 17.sp
                        )
                    )

                    Spacer(
                        modifier = GlanceModifier.width(7.dp)
                    )

                    Text(
                        text = dayName,
                        style = TextStyle(
                            color = ColorProvider(
                                day = Color(0xFFAAB5C8),
                                night = Color(0xFFAAB5C8)
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(
                    modifier = GlanceModifier.height(3.dp)
                )

                Text(
                    text = date,
                    style = TextStyle(
                        color = ColorProvider(
                            day = Color(0xFFF3F5F8),
                            night = Color(0xFFF3F5F8)
                        ),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            ProgressCircle(
                completed = completedCount,
                total = totalCount
            )
        }

        Spacer(
            modifier = GlanceModifier.height(4.dp)
        )

        /* ---------- HABITS ---------- */
        LazyColumn(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight()
        ) {
            habits.forEachIndexed { index, habit ->

                item {
                    HabitCard(habit)
                }

                if (index < habits.lastIndex) {
                    item {
                        Spacer(
                            modifier = GlanceModifier.height(8.dp)
                        )
                    }
                }
            }
        }
    }
}


/* ---------------------------------------------------------
   Круг прогресса
   --------------------------------------------------------- */

@androidx.compose.runtime.Composable
private fun ProgressCircle(
    completed: Int,
    total: Int
) {

    val progress =
        if (total == 0) {
            0f
        } else {
            completed.toFloat() / total.toFloat()
        }

    Box(
        modifier = GlanceModifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator(
            modifier = GlanceModifier.fillMaxSize(),
            color = ColorProvider(
                day = if (completed > 0)
                    Color(0xFF63E6B0)
                else
                    Color(0xFF30394A),

                night = if (completed > 0)
                    Color(0xFF63E6B0)
                else
                    Color(0xFF30394A)
            )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "$completed/$total",
                style = TextStyle(
                    color = ColorProvider(
                        day = Color(0xFFF3F5F8),
                        night = Color(0xFFF3F5F8)
                    ),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "привычек",
                style = TextStyle(
                    color = ColorProvider(
                        day = Color(0xFF8E9AAF),
                        night = Color(0xFF8E9AAF)
                    ),
                    fontSize = 8.sp
                )
            )
        }
    }
}


/* ---------------------------------------------------------
   Карточка привычки
   --------------------------------------------------------- */

@androidx.compose.runtime.Composable
private fun HabitCard(
    habit: HabitWidgetItem
) {
    val cardBackground = ColorProvider(
        day = Color(0xFF1B2330),
        night = Color(0xFF1B2330)
    )

    val primaryText = ColorProvider(
        day = Color(0xFFF1F3F7),
        night = Color(0xFFF1F3F7)
    )

    val secondaryText = ColorProvider(
        day = Color(0xFF8E9AAF),
        night = Color(0xFF8E9AAF)
    )

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(cardBackground)
            .cornerRadius(18.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    actionParametersOf(
                        HabitIdKey to habit.id
                    )
                )
            )
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        /* ---------- ICON ---------- */

        Text(
            text = habit.icon,
            style = TextStyle(
                fontSize = 22.sp
            )
        )

        Spacer(
            modifier = GlanceModifier.width(11.dp)
        )


        /* ---------- NAME + MINUTES ---------- */

        Column(
            modifier = GlanceModifier.defaultWeight()
        ) {

            Text(
                text = habit.name,
                maxLines = 1,
                style = TextStyle(
                    color = primaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(
                modifier = GlanceModifier.height(2.dp)
            )

            Text(
                text = "${habit.minutesSpent} мин",
                style = TextStyle(
                    color = secondaryText,
                    fontSize = 13.sp
                )
            )
        }

        Spacer(
            modifier = GlanceModifier.width(8.dp)
        )


        /* ---------- STATUS ---------- */

        StatusIndicator(
            completed = habit.completed,
            color = habit.color
        )
    }
}


/* ---------------------------------------------------------
   Статус привычки
   --------------------------------------------------------- */

@androidx.compose.runtime.Composable
private fun StatusIndicator(
    completed: Boolean,
    color: Long
) {

    if (completed) {

        Box(
            modifier = GlanceModifier
                .size(32.dp)
                .background(
                    ColorProvider(
                        day = Color(color),
                        night = Color(color)
                    )
                )
                .cornerRadius(16.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "✓",
                style = TextStyle(
                    color = ColorProvider(
                        day = Color(0xFF10251D),
                        night = Color(0xFF10251D)
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

    } else {

        Box(
            modifier = GlanceModifier
                .size(32.dp)
                .background(
                    ColorProvider(
                        day = Color(0x00111722),
                        night = Color(0x00111722)
                    )
                )
                .cornerRadius(16.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "○",
                style = TextStyle(
                    color = ColorProvider(
                        day = Color(0xFF59657A),
                        night = Color(0xFF59657A)
                    ),
                    fontSize = 27.sp
                )
            )
        }
    }
}


/* ---------------------------------------------------------
   Данные из Room
   --------------------------------------------------------- */

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