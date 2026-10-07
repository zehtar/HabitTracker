package com.example.habittracker.data.repository

import androidx.room.withTransaction
import com.example.habittracker.data.database.HabitDatabase
import com.example.habittracker.data.database.entity.HabitEntity
import com.example.habittracker.data.database.entity.HabitEntryEntity
import com.example.habittracker.data.database.entity.HabitReminderEntity
import com.example.habittracker.ui.model.*
import java.time.LocalDate
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit

class HabitRepository(
    private val database: HabitDatabase
) {
    private val habitDao = database.habitDao()

    private val habitEntryDao = database.habitEntryDao()

    private val habitReminderDao = database.habitReminderDao()
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

                val date =
                    selectedMonth.withDayOfMonth(day)

                val dateString = date.toString()

                val dayEntries = entries.filter {
                    it.date == dateString
                }

                val completed = dayEntries.count {
                    it.completed
                }

                val minutes = dayEntries.sumOf {
                    it.minutesSpent
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
                    completionPercent = completionPercent,
                    minutes = minutes
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
    fun getRemindersForHabit(
        habitId: Int
    ): Flow<List<HabitReminderEntity>> {
        return habitReminderDao.getRemindersForHabit(habitId)
    }

    fun getEnabledReminders(): Flow<List<HabitReminderEntity>> {
        return habitReminderDao.getEnabledReminders()
    }

    suspend fun getReminderById(
        id: Int
    ): HabitReminderEntity? {
        return habitReminderDao.getReminderById(id)
    }

    suspend fun addReminder(
        reminder: HabitReminderEntity
    ): Long {
        return habitReminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(
        reminder: HabitReminderEntity
    ) {
        habitReminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(
        reminder: HabitReminderEntity
    ) {
        habitReminderDao.deleteReminder(reminder)
    }

    suspend fun deleteRemindersForHabit(
        habitId: Int
    ) {
        habitReminderDao.deleteRemindersForHabit(habitId)
    }

    fun getAllHabits(): Flow<List<HabitEntity>> {
        return habitDao.getAllHabits()
    }
    fun getAllReminders(): Flow<List<HabitReminderEntity>> {
        return habitReminderDao.getAllReminders()
    }

    fun getFullStatistics(
        period: Flow<FullStatisticsPeriod>,
        selectedHabitId: Flow<Int?>
    ): Flow<FullStatisticsUiModel> {

        return combine(
            habitDao.getAllHabits(),
            habitEntryDao.getAllEntries(),
            period,
            selectedHabitId
        ) { habits, entries, selectedPeriod, habitId ->

            val today = LocalDate.now()

            val validEntries = entries.mapNotNull { entry ->
                runCatching {
                    entry to LocalDate.parse(entry.date)
                }.getOrNull()
            }

            fun getPeriodStart(): LocalDate {
                return when (selectedPeriod) {

                    FullStatisticsPeriod.ALL_TIME -> {
                        validEntries
                            .minOfOrNull { it.second }
                            ?: today
                    }

                    FullStatisticsPeriod.DAY -> {
                        today
                    }

                    FullStatisticsPeriod.WEEK -> {
                        today.with(DayOfWeek.MONDAY)
                    }

                    FullStatisticsPeriod.MONTH -> {
                        today.withDayOfMonth(1)
                    }

                    FullStatisticsPeriod.YEAR -> {
                        today.withDayOfYear(1)
                    }
                }
            }

            fun getPeriodEnd(): LocalDate {
                return when (selectedPeriod) {

                    FullStatisticsPeriod.ALL_TIME -> {
                        today
                    }

                    FullStatisticsPeriod.DAY -> {
                        today
                    }

                    FullStatisticsPeriod.WEEK -> {
                        today.with(DayOfWeek.MONDAY)
                            .plusDays(6)
                    }

                    FullStatisticsPeriod.MONTH -> {
                        today.withDayOfMonth(
                            today.lengthOfMonth()
                        )
                    }

                    FullStatisticsPeriod.YEAR -> {
                        today.withDayOfYear(
                            today.lengthOfYear()
                        )
                    }
                }
            }

            val periodStart = getPeriodStart()
            val periodEnd = getPeriodEnd()

            val periodDays =
                if (periodEnd.isBefore(periodStart)) {
                    0
                } else {
                    ChronoUnit.DAYS.between(
                        periodStart,
                        periodEnd
                    ).toInt() + 1
                }

            val periodEntries =
                validEntries.filter { (_, date) ->
                    !date.isBefore(periodStart) &&
                            !date.isAfter(periodEnd)
                }

            val completed =
                periodEntries
                    .filter { (entry, _) ->
                        entry.completed
                    }
                    .distinctBy { (entry, date) ->
                        entry.habitId to date
                    }
                    .size

            val totalMinutes =
                periodEntries.sumOf { (entry, _) ->
                    entry.minutesSpent
                }

            val possibleCompletions =
                habits.size * periodDays

            val completionPercent =
                if (possibleCompletions == 0) {
                    0
                } else {
                    completed * 100 / possibleCompletions
                }

            val averageMinutesPerCompletion =
                if (completed == 0) {
                    0
                } else {
                    totalMinutes / completed
                }

            val averageMinutesPerDay =
                if (periodDays == 0) {
                    0
                } else {
                    totalMinutes / periodDays
                }

            val activity =
                if (periodDays <= 0) {
                    emptyList()
                } else {
                    (0 until periodDays).map { offset ->

                        val date =
                            periodStart.plusDays(
                                offset.toLong()
                            )

                        val dayEntries =
                            periodEntries.filter { (_, entryDate) ->
                                entryDate == date
                            }

                        val dayCompleted =
                            dayEntries
                                .filter { (entry, _) ->
                                    entry.completed
                                }
                                .distinctBy { (entry, _) ->
                                    entry.habitId
                                }
                                .size

                        val dayMinutes =
                            dayEntries.sumOf { (entry, _) ->
                                entry.minutesSpent
                            }

                        val dayCompletionPercent =
                            if (habits.isEmpty()) {
                                0
                            } else {
                                dayCompleted * 100 / habits.size
                            }

                        FullStatisticsDayUiModel(
                            date = date,
                            completed = dayCompleted,
                            total = habits.size,
                            completionPercent = dayCompletionPercent,
                            minutes = dayMinutes
                        )
                    }
                }

            val currentMonth =
                today.withDayOfMonth(1)

            val previousMonth =
                currentMonth.minusMonths(1)

            val currentMonthEnd =
                currentMonth.withDayOfMonth(
                    currentMonth.lengthOfMonth()
                )

            val previousMonthEnd =
                previousMonth.withDayOfMonth(
                    previousMonth.lengthOfMonth()
                )

            val currentMonthEntries =
                validEntries.filter { (_, date) ->
                    !date.isBefore(currentMonth) &&
                            !date.isAfter(currentMonthEnd)
                }

            val previousMonthEntries =
                validEntries.filter { (_, date) ->
                    !date.isBefore(previousMonth) &&
                            !date.isAfter(previousMonthEnd)
                }

            val currentMonthCompleted =
                currentMonthEntries.count { (entry, _) ->
                    entry.completed
                }

            val previousMonthCompleted =
                previousMonthEntries.count { (entry, _) ->
                    entry.completed
                }

            val currentMonthMinutes =
                currentMonthEntries.sumOf { (entry, _) ->
                    entry.minutesSpent
                }

            val previousMonthMinutes =
                previousMonthEntries.sumOf { (entry, _) ->
                    entry.minutesSpent
                }

            fun calculateChangePercent(
                oldValue: Int,
                newValue: Int
            ): Int {
                if (oldValue == 0) {
                    return if (newValue == 0) {
                        0
                    } else {
                        100
                    }
                }

                return (newValue - oldValue) * 100 / oldValue
            }

            val monthlyComparison =
                MonthlyComparisonUiModel(

                    currentMonth = currentMonth,

                    previousMonth = previousMonth,

                    currentCompleted =
                        currentMonthCompleted,

                    previousCompleted =
                        previousMonthCompleted,

                    currentMinutes =
                        currentMonthMinutes,

                    previousMinutes =
                        previousMonthMinutes,

                    completedChangePercent =
                        calculateChangePercent(
                            previousMonthCompleted,
                            currentMonthCompleted
                        ),

                    minutesChangePercent =
                        calculateChangePercent(
                            previousMonthMinutes,
                            currentMonthMinutes
                        )
                )

            val selectedHabit =
                habits.find {
                    it.id == habitId
                }

            val selectedHabitEntries =
                if (selectedHabit == null) {
                    emptyList()
                } else {
                    validEntries.filter { (entry, _) ->
                        entry.habitId == selectedHabit.id
                    }
                }

            val selectedHabitPeriodEntries =
                selectedHabitEntries.filter { (_, date) ->
                    !date.isBefore(periodStart) &&
                            !date.isAfter(periodEnd)
                }

            val habitCompleted =
                selectedHabitPeriodEntries
                    .filter { (entry, _) ->
                        entry.completed
                    }
                    .distinctBy { (_, date) ->
                        date
                    }
                    .size

            val habitMinutes =
                selectedHabitPeriodEntries.sumOf { (entry, _) ->
                    entry.minutesSpent
                }

            val habitCompletionPercent =
                if (periodDays == 0) {
                    0
                } else {
                    (
                            habitCompleted * 100 / periodDays
                            ).coerceIn(0, 100)
                }

            val habitAverageMinutesPerCompletion =
                if (habitCompleted == 0) {
                    0
                } else {
                    habitMinutes / habitCompleted
                }

            val habitAverageMinutesPerDay =
                if (periodDays == 0) {
                    0
                } else {
                    habitMinutes / periodDays
                }

            val completedDates =
                selectedHabitEntries
                    .filter { (entry, _) ->
                        entry.completed
                    }
                    .map { (_, date) ->
                        date
                    }
                    .toSet()

            var currentStreak = 0

            var streakDate = today

            while (completedDates.contains(streakDate)) {
                currentStreak++

                streakDate =
                    streakDate.minusDays(1)
            }

            val sortedCompletedDates =
                completedDates.sorted()

            var bestStreak = 0
            var currentBestStreak = 0
            var previousDate: LocalDate? = null

            sortedCompletedDates.forEach { date ->

                if (
                    previousDate != null &&
                    date == previousDate!!.plusDays(1)
                ) {
                    currentBestStreak++
                } else {
                    currentBestStreak = 1
                }

                if (currentBestStreak > bestStreak) {
                    bestStreak = currentBestStreak
                }

                previousDate = date
            }

            val habitStatistics =
                selectedHabit?.let { habit ->

                    HabitFullStatisticsUiModel(
                        habitId = habit.id,
                        name = habit.name,
                        icon = habit.icon,
                        color = habit.color,

                        currentStreak = currentStreak,

                        bestStreak = bestStreak,

                        completed = habitCompleted,

                        completionPercent =
                            habitCompletionPercent,

                        totalMinutes = habitMinutes,

                        averageMinutesPerCompletion =
                            habitAverageMinutesPerCompletion,

                        averageMinutesPerDay =
                            habitAverageMinutesPerDay
                    )
                }

            val habitCalendar =
                selectedHabit?.let { habit ->

                    val firstDate =
                        selectedHabitEntries
                            .minOfOrNull { (_, date) ->
                                date
                            }
                            ?: today

                    val daysCount =
                        java.time.temporal.ChronoUnit.DAYS
                            .between(firstDate, today)
                            .toInt()

                    (0..daysCount).map { offset ->

                        val date =
                            firstDate.plusDays(
                                offset.toLong()
                            )

                        val entry =
                            selectedHabitEntries.find { (_, entryDate) ->
                                entryDate == date
                            }?.first

                        HabitCalendarDayUiModel(
                            date = date,
                            completed =
                                entry?.completed ?: false,
                            minutes =
                                entry?.minutesSpent ?: 0
                        )
                    }
                } ?: emptyList()

            FullStatisticsUiModel(

                period = selectedPeriod,

                overall =
                    FullStatisticsOverallUiModel(
                        completed = completed,
                        totalMinutes = totalMinutes,
                        completionPercent = completionPercent,
                        averageMinutesPerCompletion =
                            averageMinutesPerCompletion,
                        averageMinutesPerDay =
                            averageMinutesPerDay
                    ),

                activity = activity,

                monthlyComparison =
                    monthlyComparison,

                selectedHabit =
                    habitStatistics,

                habitCalendar =
                    habitCalendar
            )
        }
    }
}