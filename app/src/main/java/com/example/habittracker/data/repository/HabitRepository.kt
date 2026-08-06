package com.example.habittracker.data.repository

import com.example.habittracker.data.database.HabitDatabase
import com.example.habittracker.data.database.entity.HabitEntity
import com.example.habittracker.data.database.entity.HabitEntryEntity
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
        habitDao.deleteHabitById(id)
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

            val weekStart = date.with(DayOfWeek.MONDAY)

            val weekEnd = weekStart.plusDays(6)

            val selectedDateString = date.toString()

            val completedToday = entries.count {

                it.date == selectedDateString &&
                        it.completed

            }

            val totalHabits = habits.size

            val todayMinutes = entries

                .filter {

                    it.date == selectedDateString

                }

                .sumOf {

                    it.minutesSpent

                }

            val weekEntries = entries.filter {

                val entryDate = LocalDate.parse(it.date)

                !entryDate.isBefore(weekStart) &&
                        !entryDate.isAfter(weekEnd)

            }

            val completedWeek = weekEntries.count {

                it.completed

            }

            val weekMinutes = weekEntries.sumOf {

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

            return@combine StatisticsUiModel(

                selectedDate = date,

                completedToday = completedToday,

                totalHabits = totalHabits,

                completedWeek = completedWeek,

                totalMinutesToday = todayMinutes,

                totalMinutesWeek = weekMinutes,

                completionPercent = completionPercent,

                averageMinutesPerDay = averageMinutesPerDay

            )
        }
    }
}