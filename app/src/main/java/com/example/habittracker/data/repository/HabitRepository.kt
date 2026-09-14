package com.example.habittracker.data.repository

import androidx.room.withTransaction
import com.example.habittracker.data.database.HabitDatabase
import com.example.habittracker.data.database.entity.HabitEntity
import com.example.habittracker.data.database.entity.HabitEntryEntity
import com.example.habittracker.ui.model.CalendarDayHabitUiModel
import com.example.habittracker.ui.model.CalendarDayUiModel
import com.example.habittracker.ui.model.DailyStatisticUiModel
import com.example.habittracker.ui.model.HabitTimeStatisticUiModel
import java.time.LocalDate
import com.example.habittracker.ui.model.HabitUiModel
import com.example.habittracker.ui.model.StatisticsUiModel
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek

class HabitRepository(
    private val database: HabitDatabase
) {
    private val habitDao = database.habitDao()

    private val habitEntryDao = database.habitEntryDao()

    fun getHabitsForDate(
        selectedDate: Flow<LocalDate>
    ): Flow<List<HabitUiModel>> {

        val entriesForDate =
            selectedDate.flatMapLatest { date ->

                habitEntryDao.getEntriesForDate(
                    date.toString()
                )
            }

        return combine(
            habitDao.getAllHabits(),
            entriesForDate

        ) { habits, entries ->

            habits.map { habit ->
                val entry = entries.find {
                    it.habitId == habit.id
                }
                HabitUiModel(
                    id = habit.id,
                    name = habit.name,
                    icon = habit.icon,
                    color = habit.color,
                    completed = entry?.completed ?: false,
                    minutesSpent = entry?.minutesSpent ?: 0
                )
            }
        }
    }

    suspend fun updateEntry(entry: HabitEntryEntity) {
        habitEntryDao.upsertEntry(entry)
    }


    suspend fun updateHabit(
        id: Int,
        name: String,
        icon: String,
        color: Long
    ) {

        habitDao.upsertHabit(
            HabitEntity(
                id = id,
                name = name,
                icon = icon,
                color = color
            )
        )

    }
    suspend fun addHabit(
        name: String,
        icon: String,
        color: Long
    ) {
        habitDao.upsertHabit(
            HabitEntity(
                name = name,
                icon = icon,
                color = color
            )
        )
    }
    suspend fun deleteHabit(id: Int) {

        database.withTransaction {

            habitEntryDao.deleteEntriesForHabit(id)

            habitDao.deleteHabitById(id)
        }
    }

    suspend fun updateHabitEntry(

        habitId: Int,

        date: LocalDate,

        completed: Boolean,

        minutesSpent: Int

    ) {

        val oldEntry = habitEntryDao.getEntryForDay(

            habitId,
            date.toString()

        )

        if (oldEntry != null) {

            habitEntryDao.upsertEntry(

                oldEntry.copy(

                    completed = completed,

                    minutesSpent = minutesSpent

                )

            )

        } else {

            habitEntryDao.upsertEntry(

                HabitEntryEntity(

                    habitId = habitId,

                    date = date.toString(),

                    completed = completed,

                    minutesSpent = minutesSpent

                )

            )

        }

    }

    fun getStatistics(
        selectedDate: StateFlow<LocalDate>
    ): Flow<StatisticsUiModel> {

        return combine(
            habitDao.getAllHabits(),
            habitEntryDao.getAllEntries(),
            selectedDate
        ) { habits, entries, date ->

            val weekStart =
                date.with(DayOfWeek.MONDAY)

            val weekEnd =
                weekStart.plusDays(6)

            val selectedDateString =
                date.toString()

            val totalHabits =
                habits.size

            val completedToday =
                entries.count {
                    it.date == selectedDateString &&
                            it.completed
                }

            val todayMinutes =
                entries
                    .filter {
                        it.date == selectedDateString
                    }
                    .sumOf {
                        it.minutesSpent
                    }

            val weekEntries =
                entries.filter {

                    val entryDate =
                        LocalDate.parse(it.date)

                    !entryDate.isBefore(weekStart) &&
                            !entryDate.isAfter(weekEnd)
                }

            val completedWeek =
                weekEntries.count {
                    it.completed
                }

            val weekMinutes =
                weekEntries.sumOf {
                    it.minutesSpent
                }

            val completionPercent =
                if (totalHabits == 0) {
                    0
                } else {
                    completedToday * 100 / totalHabits
                }

            val averageMinutesPerDay =
                weekMinutes / 7

            val dailyStatistics =
                (0..6).map { dayOffset ->

                    val currentDate =
                        weekStart.plusDays(dayOffset.toLong())

                    val currentDateString =
                        currentDate.toString()

                    val completed =
                        entries.count {
                            it.date == currentDateString &&
                                    it.completed
                        }

                    val percent =
                        if (totalHabits == 0) {
                            0
                        } else {
                            completed * 100 / totalHabits
                        }

                    val minutes =
                        entries
                            .filter {
                                it.date == currentDateString
                            }
                            .sumOf {
                                it.minutesSpent
                            }

                    DailyStatisticUiModel(
                        date = currentDate,
                        completed = completed,
                        total = totalHabits,
                        completionPercent = percent,
                        minutes = minutes
                    )
                }

            StatisticsUiModel(

                selectedDate = date,

                completedToday = completedToday,

                totalHabits = totalHabits,

                completedWeek = completedWeek,

                totalMinutesToday = todayMinutes,

                totalMinutesWeek = weekMinutes,

                completionPercent = completionPercent,

                averageMinutesPerDay = averageMinutesPerDay,

                dailyStatistics = dailyStatistics
            )
        }
    }
    fun getCalendarDayDetails(
        selectedDate: Flow<LocalDate>
    ): Flow<List<CalendarDayHabitUiModel>> {
        return combine(
            habitDao.getAllHabits(),
            habitEntryDao.getAllEntries(),
            selectedDate
        ) { habits, entries, date ->

            val dateString = date.toString()

            entries
                .filter {
                    it.date == dateString &&
                            it.completed
                }
                .mapNotNull { entry ->

                    habits.find {
                        it.id == entry.habitId
                    }?.let { habit ->

                        CalendarDayHabitUiModel(
                            habitId = habit.id,
                            name = habit.name,
                            icon = habit.icon,
                            color = habit.color,
                            minutesSpent = entry.minutesSpent
                        )
                    }
                }
        }
    }
    fun getCalendarStatistics(
        month: StateFlow<LocalDate>
    ): Flow<List<CalendarDayUiModel>> {
        return combine(
            habitDao.getAllHabits(),
            habitEntryDao.getAllEntries(),
            month
        ) { habits, entries, selectedMonth ->

            val totalHabits = habits.size

            (1..selectedMonth.lengthOfMonth()).map { day ->
                val date = selectedMonth
                    .withDayOfMonth(day)

                val dateString = date.toString()

                val completed = entries.count {
                    it.date == dateString &&
                            it.completed
                }

                val completionPercent =
                    if (totalHabits == 0) {
                        0
                    } else {
                        completed * 100 / totalHabits
                    }

                CalendarDayUiModel(
                    date = date,
                    completed = completed,
                    total = totalHabits,
                    completionPercent = completionPercent
                )
            }
        }
    }

    fun getHabitTimeForWeek(
        weekStart: Flow<LocalDate>
    ): Flow<List<HabitTimeStatisticUiModel>> {
        return combine(
            habitDao.getAllHabits(),
            habitEntryDao.getAllEntries(),
            weekStart
        ) { habits, entries, startDate ->

            val weekEnd = startDate.plusDays(6)

            habits.mapNotNull { habit ->

                val minutes = entries
                    .filter { entry ->
                        entry.habitId == habit.id &&
                                runCatching {
                                    val date = LocalDate.parse(entry.date)

                                    !date.isBefore(startDate) &&
                                            !date.isAfter(weekEnd)
                                }.getOrDefault(false)
                    }
                    .sumOf { it.minutesSpent }

                if (minutes > 0) {
                    HabitTimeStatisticUiModel(
                        habitId = habit.id,
                        name = habit.name,
                        icon = habit.icon,
                        color = habit.color,
                        minutes = minutes
                    )
                } else {
                    null
                }
            }.sortedByDescending {
                it.minutes
            }
        }
    }
    fun getHabitTimeForMonth(
        month: Flow<LocalDate>
    ): Flow<List<HabitTimeStatisticUiModel>> {
        return combine(
            habitDao.getAllHabits(),
            habitEntryDao.getAllEntries(),
            month
        ) { habits, entries, selectedMonth ->

            val firstDay =
                selectedMonth.withDayOfMonth(1)

            val lastDay =
                selectedMonth.withDayOfMonth(
                    selectedMonth.lengthOfMonth()
                )

            habits.mapNotNull { habit ->

                val minutes = entries
                    .filter { entry ->
                        entry.habitId == habit.id &&
                                runCatching {
                                    val date = LocalDate.parse(entry.date)

                                    !date.isBefore(firstDay) &&
                                            !date.isAfter(lastDay)
                                }.getOrDefault(false)
                    }
                    .sumOf { it.minutesSpent }

                if (minutes > 0) {
                    HabitTimeStatisticUiModel(
                        habitId = habit.id,
                        name = habit.name,
                        icon = habit.icon,
                        color = habit.color,
                        minutes = minutes
                    )
                } else {
                    null
                }
            }.sortedByDescending {
                it.minutes
            }
        }
    }
}