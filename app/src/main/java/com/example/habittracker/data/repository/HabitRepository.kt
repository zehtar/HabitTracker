package com.example.habittracker.data.repository

import androidx.room.withTransaction
import com.example.habittracker.data.database.HabitDatabase
import com.example.habittracker.data.database.entity.*
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

    private val habitPauseDao = database.habitPauseDao()

    suspend fun pauseHabit(
        habitId: Int,
        endDate: LocalDate? = null
    ) {
        val today = LocalDate.now()

        require(endDate == null || !endDate.isBefore(today)) {
            "Дата окончания паузы не может быть в прошлом"
        }

        val existingPause = habitPauseDao.getActivePause(
            habitId = habitId,
            date = today.toString()
        )

        if (existingPause != null) return

        habitPauseDao.insertPause(
            HabitPauseEntity(
                habitId = habitId,
                startDate = today.toString(),
                endDate = endDate?.toString()
            )
        )
    }

    fun getPausesForHabit(
        habitId: Int
    ): Flow<List<HabitPauseEntity>> {
        return habitPauseDao.observePausesForHabit(habitId)
    }

    suspend fun getActivePause(
        habitId: Int
    ): HabitPauseEntity? {
        return habitPauseDao.getActivePause(
            habitId = habitId,
            date = LocalDate.now().toString()
        )
    }

    suspend fun resumeHabit(habitId: Int) {
        val today = LocalDate.now()

        val activePause = habitPauseDao.getActivePause(
            habitId = habitId,
            date = today.toString()
        ) ?: return

        val startDate = LocalDate.parse(activePause.startDate)

        if (startDate == today) {
            // Если пауза началась сегодня, удаляем её.
            habitPauseDao.deletePause(activePause.id)
        } else {
            // Завершаем паузу вчерашним днём.
            habitPauseDao.finishPause(
                pauseId = activePause.id,
                endDate = today.minusDays(1).toString()
            )
        }
    }

    suspend fun resetHabitStreak(habitId: Int) {
        val habit = habitDao.getHabitById(habitId) ?: return

        habitDao.upsertHabit(
            habit.copy(
                streakResetDate = LocalDate.now().toString()
            )
        )
    }
    fun getHabitsForDate(
        selectedDate: Flow<LocalDate>
    ): Flow<List<HabitUiModel>> {

        val entriesForDate = selectedDate.flatMapLatest { date ->
            habitEntryDao.getEntriesForDate(date.toString())
        }

        return combine(
            habitDao.getAllHabits(),
            entriesForDate,
            habitPauseDao.getAllPauses()
        ) { habits, entries, pauses ->

            val today = LocalDate.now()

            habits.map { habit ->
                val entry = entries.find {
                    it.habitId == habit.id
                }

                val isPausedToday = pauses.any { pause ->
                    if (pause.habitId != habit.id) {
                        false
                    } else {
                        val startDate = runCatching {
                            LocalDate.parse(pause.startDate)
                        }.getOrNull()

                        val endDate = pause.endDate?.let {
                            runCatching {
                                LocalDate.parse(it)
                            }.getOrNull()
                        }

                        startDate != null &&
                                !today.isBefore(startDate) &&
                                (endDate == null || !today.isAfter(endDate))
                    }
                }

                HabitUiModel(
                    id = habit.id,
                    name = habit.name,
                    icon = habit.icon,
                    color = habit.color,
                    completed = entry?.completed ?: false,
                    minutesSpent = entry?.minutesSpent ?: 0,
                    habitType = habit.habitType,
                    guideline = habit.guideline,
                    count = entry?.count ?: 0,
                    category = habit.category,
                    priority = habit.priority,
                    isPaused = isPausedToday
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
        color: Long,
        habitType: String,
        guideline: String?,
        category: String?,
        priority: String
    ) {
        val existingHabit = habitDao.getHabitById(id)
            ?: return

        habitDao.upsertHabit(
            existingHabit.copy(
                name = name,
                icon = icon,
                color = color,
                habitType = habitType,
                guideline = guideline,
                category = category,
                priority = priority
            )
        )
    }

    suspend fun addHabit(
        name: String,
        icon: String,
        color: Long,
        habitType: String = "TIME",
        guideline: String? = null,
        category: String? = null,
        priority: String = "IMPORTANT"
    ) {
        habitDao.upsertHabit(
            HabitEntity(
                name = name,
                icon = icon,
                color = color,
                habitType = habitType,
                guideline = guideline,
                category = category,
                priority = priority
            )
        )
    }

    suspend fun deleteHabit(id: Int) {
        database.withTransaction {
            habitEntryDao.deleteEntriesForHabit(id)
            habitPauseDao.deletePausesForHabit(id)
            habitDao.deleteHabitById(id)
        }
    }

    suspend fun updateHabitEntry(
        habitId: Int,
        date: LocalDate,
        completed: Boolean,
        minutesSpent: Int,
        count: Int
    ) {
        val oldEntry = habitEntryDao.getEntryForDay(
            habitId = habitId,
            date = date.toString(),
        )

        if (oldEntry != null) {
            habitEntryDao.upsertEntry(
                oldEntry.copy(
                    completed = completed,
                    minutesSpent = minutesSpent,
                    count = count
                )
            )
        } else {
            habitEntryDao.upsertEntry(
                HabitEntryEntity(
                    habitId = habitId,
                    date = date.toString(),
                    completed = completed,
                    minutesSpent = minutesSpent,
                    count = count ?: 0
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

            val habitsById = habits.associateBy { it.id }
            val weekStart = date.with(DayOfWeek.MONDAY)
            val selectedDateString = date.toString()

            fun isCompleted(entry: HabitEntryEntity): Boolean {
                val habit = habitsById[entry.habitId] ?: return false

                return when (habit.habitType) {
                    "CHECK" -> entry.completed
                    "TIME" -> entry.minutesSpent > 0
                    "COUNTER" -> entry.count > 0
                    else -> entry.completed
                }
            }

            fun minutesFor(entriesForPeriod: List<HabitEntryEntity>): Int {
                return entriesForPeriod.sumOf { entry ->
                    if (habitsById[entry.habitId]?.habitType == "TIME") {
                        entry.minutesSpent.coerceAtLeast(0)
                    } else {
                        0
                    }
                }
            }

            val todayEntries = entries.filter {
                it.date == selectedDateString &&
                        habitsById.containsKey(it.habitId)
            }

            val completedToday = todayEntries.count(::isCompleted)
            val todayMinutes = minutesFor(todayEntries)

            val totalHabits = habits.size

            val weekEntries = entries.filter { entry ->
                val entryDate = runCatching {
                    LocalDate.parse(entry.date)
                }.getOrNull()

                entryDate != null &&
                        !entryDate.isBefore(weekStart) &&
                        !entryDate.isAfter(weekStart.plusDays(6)) &&
                        habitsById.containsKey(entry.habitId)
            }

            val completedWeek = weekEntries.count(::isCompleted)
            val weekMinutes = minutesFor(weekEntries)

            val completionPercent = if (totalHabits == 0) {
                0
            } else {
                completedToday * 100 / totalHabits
            }

            val dailyStatistics = (0..6).map { dayOffset ->
                val currentDate = weekStart.plusDays(dayOffset.toLong())
                val currentDateString = currentDate.toString()

                val dayEntries = entries.filter { entry ->
                    entry.date == currentDateString &&
                            habitsById.containsKey(entry.habitId)
                }

                val completed = dayEntries.count(::isCompleted)

                val percent = if (totalHabits == 0) {
                    0
                } else {
                    completed * 100 / totalHabits
                }

                DailyStatisticUiModel(
                    date = currentDate,
                    completed = completed,
                    total = totalHabits,
                    completionPercent = percent,
                    minutes = minutesFor(dayEntries)
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
                averageMinutesPerDay = weekMinutes / 7,
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

            val habitsById = habits.associateBy { it.id }
            val dateString = date.toString()

            entries
                .filter { entry ->
                    if (entry.date != dateString) {
                        return@filter false
                    }

                    val habit = habitsById[entry.habitId]
                        ?: return@filter false

                    when (habit.habitType) {
                        "CHECK" -> entry.completed
                        "TIME" -> entry.minutesSpent > 0
                        "COUNTER" -> entry.count > 0
                        else -> entry.completed
                    }
                }
                .mapNotNull { entry ->
                    habitsById[entry.habitId]?.let { habit ->

                        CalendarDayHabitUiModel(
                            habitId = habit.id,
                            name = habit.name,
                            icon = habit.icon,
                            color = habit.color,
                            habitType = habit.habitType.uppercase(),
                            minutesSpent = if (
                                habit.habitType.equals(
                                    "TIME",
                                    ignoreCase = true
                                )
                            ) {
                                entry.minutesSpent.coerceAtLeast(0)
                            } else {
                                0
                            },
                            count = if (
                                habit.habitType.equals(
                                    "COUNTER",
                                    ignoreCase = true
                                )
                            ) {
                                entry.count.coerceAtLeast(0)
                            } else {
                                0
                            }
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

            val habitsById = habits.associateBy { it.id }
            val totalHabits = habits.size

            (1..selectedMonth.lengthOfMonth()).map { day ->
                val date = selectedMonth.withDayOfMonth(day)
                val dateString = date.toString()

                val dayEntries = entries.filter { entry ->
                    entry.date == dateString &&
                            habitsById.containsKey(entry.habitId)
                }

                val completed = dayEntries.count { entry ->
                    val habit = habitsById[entry.habitId]

                    when (habit?.habitType) {
                        "CHECK" -> entry.completed
                        "TIME" -> entry.minutesSpent > 0
                        "COUNTER" -> entry.count > 0
                        else -> entry.completed
                    }
                }

                val minutes = dayEntries.sumOf { entry ->
                    if (habitsById[entry.habitId]?.habitType == "TIME") {
                        entry.minutesSpent.coerceAtLeast(0)
                    } else {
                        0
                    }
                }

                val completionPercent = if (totalHabits == 0) {
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

            habits
                .filter { it.habitType == "TIME" }
                .mapNotNull { habit ->

                    val minutes = entries
                        .filter { entry ->
                            entry.habitId == habit.id &&
                                    runCatching {
                                        val date = LocalDate.parse(entry.date)

                                        !date.isBefore(startDate) &&
                                                !date.isAfter(weekEnd)
                                    }.getOrDefault(false)
                        }
                        .sumOf { it.minutesSpent.coerceAtLeast(0) }

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
                }
                .sortedByDescending { it.minutes }
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

            val firstDay = selectedMonth.withDayOfMonth(1)

            val lastDay = selectedMonth.withDayOfMonth(
                selectedMonth.lengthOfMonth()
            )

            habits
                .filter { it.habitType == "TIME" }
                .mapNotNull { habit ->

                    val minutes = entries
                        .filter { entry ->
                            entry.habitId == habit.id &&
                                    runCatching {
                                        val date = LocalDate.parse(entry.date)

                                        !date.isBefore(firstDay) &&
                                                !date.isAfter(lastDay)
                                    }.getOrDefault(false)
                        }
                        .sumOf { it.minutesSpent.coerceAtLeast(0) }

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
                }
                .sortedByDescending { it.minutes }
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
            habitPauseDao.getAllPauses(),
            period,
            selectedHabitId
        ) { habits, entries, pauses, selectedPeriod, habitId ->

            val today = LocalDate.now()
            val habitsById = habits.associateBy { it.id }

            fun isCompleted(entry: HabitEntryEntity): Boolean {
                val habit = habitsById[entry.habitId] ?: return false

                return when (habit.habitType) {
                    "CHECK" -> entry.completed
                    "TIME" -> entry.minutesSpent > 0
                    "COUNTER" -> entry.count > 0
                    else -> entry.completed
                }
            }

            fun getMinutes(entry: HabitEntryEntity): Int {
                return if (habitsById[entry.habitId]?.habitType == "TIME") {
                    entry.minutesSpent.coerceAtLeast(0)
                } else {
                    0
                }
            }

            fun getCount(entry: HabitEntryEntity): Int {
                return if (
                    habitsById[entry.habitId]
                        ?.habitType
                        ?.equals("COUNTER", ignoreCase = true) == true
                ) {
                    entry.count.coerceAtLeast(0)
                } else {
                    0
                }
            }


            val validEntries = entries.mapNotNull { entry ->
                if (!habitsById.containsKey(entry.habitId)) {
                    null
                } else {
                    runCatching {
                        entry to LocalDate.parse(entry.date)
                    }.getOrNull()
                }
            }

            fun getPeriodStart(): LocalDate {
                return when (selectedPeriod) {
                    FullStatisticsPeriod.ALL_TIME -> {
                        validEntries.minOfOrNull { it.second } ?: today
                    }

                    FullStatisticsPeriod.DAY -> today

                    FullStatisticsPeriod.WEEK ->
                        today.with(DayOfWeek.MONDAY)

                    FullStatisticsPeriod.MONTH ->
                        today.withDayOfMonth(1)

                    FullStatisticsPeriod.YEAR ->
                        today.withDayOfYear(1)
                }
            }

            fun getPeriodEnd(): LocalDate {
                return when (selectedPeriod) {
                    FullStatisticsPeriod.ALL_TIME -> today

                    FullStatisticsPeriod.DAY -> today

                    FullStatisticsPeriod.WEEK ->
                        today.with(DayOfWeek.MONDAY).plusDays(6)

                    FullStatisticsPeriod.MONTH ->
                        today.withDayOfMonth(today.lengthOfMonth())

                    FullStatisticsPeriod.YEAR ->
                        today.withDayOfYear(today.lengthOfYear())
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

            val periodEntries = validEntries.filter { (_, date) ->
                !date.isBefore(periodStart) && !date.isAfter(periodEnd)
            }

            val completedEntries = periodEntries
                .filter { (entry, _) -> isCompleted(entry) }
                .distinctBy { (entry, date) -> entry.habitId to date }

            val completed = completedEntries.size

            val totalMinutes = periodEntries.sumOf { (entry, _) ->
                getMinutes(entry)
            }

            val totalCount = periodEntries.sumOf { (entry, _) ->
                getCount(entry)
            }


            val timeCompletions = completedEntries.count { (entry, _) ->
                habitsById[entry.habitId]?.habitType == "TIME" &&
                        entry.minutesSpent > 0
            }

            val possibleCompletions = habits.size * periodDays

            val completionPercent =
                if (possibleCompletions == 0) {
                    0
                } else {
                    completed * 100 / possibleCompletions
                }

            val averageMinutesPerCompletion =
                if (timeCompletions == 0) {
                    0
                } else {
                    totalMinutes / timeCompletions
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

                        val date = periodStart.plusDays(offset.toLong())

                        val dayEntries = periodEntries.filter { (_, entryDate) ->
                            entryDate == date
                        }

                        val dayCompleted = dayEntries
                            .filter { (entry, _) -> isCompleted(entry) }
                            .distinctBy { (entry, _) -> entry.habitId }
                            .size

                        val dayMinutes = dayEntries.sumOf { (entry, _) ->
                            getMinutes(entry)
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

            val currentMonth = today.withDayOfMonth(1)
            val previousMonth = currentMonth.minusMonths(1)

            val currentMonthEnd =
                currentMonth.withDayOfMonth(currentMonth.lengthOfMonth())

            val previousMonthEnd =
                previousMonth.withDayOfMonth(previousMonth.lengthOfMonth())

            val currentMonthEntries = validEntries.filter { (_, date) ->
                !date.isBefore(currentMonth) && !date.isAfter(currentMonthEnd)
            }

            val previousMonthEntries = validEntries.filter { (_, date) ->
                !date.isBefore(previousMonth) && !date.isAfter(previousMonthEnd)
            }

            val currentMonthCompleted = currentMonthEntries
                .filter { (entry, _) -> isCompleted(entry) }
                .distinctBy { (entry, date) -> entry.habitId to date }
                .size

            val previousMonthCompleted = previousMonthEntries
                .filter { (entry, _) -> isCompleted(entry) }
                .distinctBy { (entry, date) -> entry.habitId to date }
                .size

            val currentMonthMinutes = currentMonthEntries.sumOf { (entry, _) ->
                getMinutes(entry)
            }

            val previousMonthMinutes = previousMonthEntries.sumOf { (entry, _) ->
                getMinutes(entry)
            }

            fun calculateChangePercent(
                oldValue: Int,
                newValue: Int
            ): Int {
                if (oldValue == 0) {
                    return if (newValue == 0) 0 else 100
                }

                return (newValue - oldValue) * 100 / oldValue
            }

            val monthlyComparison = MonthlyComparisonUiModel(
                currentMonth = currentMonth,
                previousMonth = previousMonth,
                currentCompleted = currentMonthCompleted,
                previousCompleted = previousMonthCompleted,
                currentMinutes = currentMonthMinutes,
                previousMinutes = previousMonthMinutes,
                completedChangePercent = calculateChangePercent(
                    previousMonthCompleted,
                    currentMonthCompleted
                ),
                minutesChangePercent = calculateChangePercent(
                    previousMonthMinutes,
                    currentMonthMinutes
                )
            )

            val selectedHabit = habitsById[habitId]

            val selectedHabitEntries =
                if (selectedHabit == null) {
                    emptyList()
                } else {
                    validEntries.filter { (entry, _) ->
                        entry.habitId == selectedHabit.id
                    }
                }

            val selectedHabitPeriodEntries = selectedHabitEntries.filter { (_, date) ->
                !date.isBefore(periodStart) && !date.isAfter(periodEnd)
            }

            val habitCompleted = selectedHabitPeriodEntries
                .filter { (entry, _) -> isCompleted(entry) }
                .distinctBy { (_, date) -> date }
                .size

            val habitMinutes = selectedHabitPeriodEntries.sumOf { (entry, _) ->
                getMinutes(entry)
            }

            val habitCompletionPercent =
                if (periodDays == 0) {
                    0
                } else {
                    (habitCompleted * 100 / periodDays).coerceIn(0, 100)
                }

            val habitTimeCompletions = selectedHabitPeriodEntries.count { (entry, _) ->
                selectedHabit?.habitType == "TIME" && entry.minutesSpent > 0
            }

            val habitAverageMinutesPerCompletion =
                if (habitTimeCompletions == 0) {
                    0
                } else {
                    habitMinutes / habitTimeCompletions
                }

            val habitAverageMinutesPerDay =
                if (periodDays == 0) {
                    0
                } else {
                    habitMinutes / periodDays
                }

            val habitCount = selectedHabitPeriodEntries.sumOf { (entry, _) ->
                getCount(entry)
            }

            val habitCountCompletions = selectedHabitPeriodEntries.count {
                    (entry, _) ->
                selectedHabit?.habitType.equals(
                    "COUNTER",
                    ignoreCase = true
                ) && entry.count > 0
            }

            val habitAverageCountPerCompletion =
                if (habitCountCompletions == 0) {
                    0
                } else {
                    habitCount / habitCountCompletions
                }

            val habitAverageCountPerDay =
                if (periodDays == 0) {
                    0
                } else {
                    habitCount / periodDays
                }

            val completedDates = selectedHabitEntries
                .filter { (entry, _) -> isCompleted(entry) }
                .map { (_, date) -> date }
                .toSet()

            val selectedHabitPauses = if (selectedHabit != null) {
                pauses.filter { it.habitId == selectedHabit.id }
            } else {
                emptyList()
            }

            fun isPaused(date: LocalDate): Boolean {
                return selectedHabitPauses.any { pause ->

                    val startDate = runCatching {
                        LocalDate.parse(pause.startDate)
                    }.getOrNull() ?: return@any false

                    val endDate = pause.endDate?.let {
                        runCatching {
                            LocalDate.parse(it)
                        }.getOrNull()
                    }

                    !date.isBefore(startDate) &&
                            (endDate == null || !date.isAfter(endDate))
                }
            }

            var currentStreak = 0
            var streakDate = today

            val streakResetDate = selectedHabit?.streakResetDate?.let {
                runCatching {
                    LocalDate.parse(it)
                }.getOrNull()
            }

            while (
                !streakDate.isBefore(
                    streakResetDate ?: LocalDate.MIN
                )
            ) {
                val isResetBoundary = streakDate == streakResetDate

                if (isPaused(streakDate)) {
                    if (isResetBoundary) {
                        break
                    }

                    streakDate = streakDate.minusDays(1)
                    continue
                }

                if (!completedDates.contains(streakDate)) {
                    break
                }

                currentStreak++

                if (isResetBoundary) {
                    break
                }

                streakDate = streakDate.minusDays(1)
            }

            var bestStreak = 0
            var currentBestStreak = 0

            val firstCompletedDate = completedDates.minOrNull()
            val lastCompletedDate = completedDates.maxOrNull()

            if (firstCompletedDate != null && lastCompletedDate != null) {

                var date: LocalDate = firstCompletedDate

                while (!date.isAfter(lastCompletedDate)) {

                    if (isPaused(date)) {
                        date = date.plusDays(1)
                        continue
                    }

                    if (completedDates.contains(date)) {
                        currentBestStreak++
                        bestStreak = maxOf(bestStreak, currentBestStreak)
                    } else {
                        currentBestStreak = 0
                    }

                    date = date.plusDays(1)
                }
            }

            val habitStatistics = selectedHabit?.let { habit ->
                HabitFullStatisticsUiModel(
                    habitId = habit.id,
                    name = habit.name,
                    icon = habit.icon,
                    color = habit.color,
                    currentStreak = currentStreak,
                    bestStreak = bestStreak,
                    completed = habitCompleted,
                    completionPercent = habitCompletionPercent,
                    totalMinutes = habitMinutes,
                    averageMinutesPerCompletion = habitAverageMinutesPerCompletion,
                    averageMinutesPerDay = habitAverageMinutesPerDay,
                    habitType = habit.habitType.uppercase(),
                    totalCount = habitCount,
                    averageCountPerCompletion = habitAverageCountPerCompletion,
                    averageCountPerDay = habitAverageCountPerDay,
                    )
            }

            val habitCalendar = selectedHabit?.let {
                val firstDate = selectedHabitEntries
                    .minOfOrNull { (_, date) -> date }
                    ?: today

                val daysCount = ChronoUnit.DAYS.between(firstDate, today)
                    .toInt()
                    .coerceAtLeast(0)

                (0..daysCount).map { offset ->
                    val date = firstDate.plusDays(offset.toLong())

                    val entry = selectedHabitEntries
                        .find { (_, entryDate) -> entryDate == date }
                        ?.first

                    HabitCalendarDayUiModel(
                        date = date,
                        completed = entry?.let { isCompleted(it) } ?: false,
                        minutes = entry?.let { getMinutes(it) } ?: 0
                    )
                }
            } ?: emptyList()

            FullStatisticsUiModel(
                period = selectedPeriod,
                overall = FullStatisticsOverallUiModel(
                    completed = completed,
                    totalCount = totalCount,
                    totalMinutes = totalMinutes,
                    completionPercent = completionPercent,
                    averageMinutesPerCompletion = averageMinutesPerCompletion,
                    averageMinutesPerDay = averageMinutesPerDay
                ),
                activity = activity,
                monthlyComparison = monthlyComparison,
                selectedHabit = habitStatistics,
                habitCalendar = habitCalendar
                )
        }
    }
}