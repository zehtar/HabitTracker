package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.ui.model.CalendarDayUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import android.content.Context
import com.example.habittracker.data.database.entity.HabitReminderEntity
import com.example.habittracker.notification.HabitReminderScheduler
import com.example.habittracker.widget.WidgetUpdater
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class HabitViewModel(
    private val repository: HabitRepository,
    private val context: Context
) : ViewModel() {
    private val _weekStart = MutableStateFlow(
        LocalDate.now().with(DayOfWeek.MONDAY)
    )
    val weekStart = _weekStart.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    val habits = repository.getHabitsForDate(selectedDate)

    val statistics = repository.getStatistics(
        selectedDate
    )

    private val _calendarMonth = MutableStateFlow(
        LocalDate.now().withDayOfMonth(1)
    )

    val calendarMonth = _calendarMonth.asStateFlow()

    val calendarStatistics =
        repository.getCalendarStatistics(
            calendarMonth
        )

    fun nextCalendarMonth() {
        _calendarMonth.value =
            _calendarMonth.value.plusMonths(1)
    }

    fun previousCalendarMonth() {
        _calendarMonth.value =
            _calendarMonth.value.minusMonths(1)
    }
    private val _calendarSelectedDay =
        MutableStateFlow<CalendarDayUiModel?>(null)

    val calendarSelectedDay =
        _calendarSelectedDay.asStateFlow()

    val calendarDayDetails =
        _calendarSelectedDay
            .filterNotNull()
            .flatMapLatest { day ->
                repository.getCalendarDayDetails(
                    flowOf(day.date)
                )
            }

    fun selectCalendarDay(
        day: CalendarDayUiModel
    ) {
        _calendarSelectedDay.value = day
    }
    val calendarWeekStart =
        _calendarSelectedDay
            .map { day ->
                (day?.date ?: LocalDate.now())
                    .with(DayOfWeek.MONDAY)
            }

    val habitTimeForWeek =
        calendarWeekStart
            .flatMapLatest { weekStart ->
                repository.getHabitTimeForWeek(
                    flowOf(weekStart)
                )
            }

    val habitTimeForMonth =
        repository.getHabitTimeForMonth(
            calendarMonth
        )

    fun updateHabit(
        id: Int,
        name: String,
        icon: String,
        color: Long
    ) {
        viewModelScope.launch {
            repository.updateHabit(
                id,
                name,
                icon,
                color
            )
        }
    }
    fun addHabit(
        name: String,
        icon: String,
        color: Long
    ) {
        viewModelScope.launch {
            repository.addHabit(
                name,
                icon,
                color
            )
        }
    }
    fun deleteHabit(id: Int) {
        viewModelScope.launch {
            repository.deleteHabit(id)
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun nextWeek() {

        val newWeek = _weekStart.value.plusWeeks(1)

        _weekStart.value = newWeek
        _selectedDate.value = newWeek

    }

    fun previousWeek() {

        val newWeek = _weekStart.value.minusWeeks(1)

        _weekStart.value = newWeek
        _selectedDate.value = newWeek

    }
    fun updateHabitEntry(
        habitId: Int,
        completed: Boolean,
        minutesSpent: Int
    ) {
        viewModelScope.launch {

            repository.updateHabitEntry(
                habitId = habitId,
                date = selectedDate.value,
                completed = completed,
                minutesSpent = minutesSpent
            )
            WidgetUpdater.update(context)
        }
    }

    fun getRemindersForHabit(
        habitId: Int
    ): Flow<List<HabitReminderEntity>> {
        return repository.getRemindersForHabit(habitId)
    }
    fun addReminder(
        habitId: Int,
        hour: Int,
        minute: Int,
        enabled: Boolean = true,
        repeatType: String = "DAILY"
    ) {
        viewModelScope.launch {

            val reminder = HabitReminderEntity(
                habitId = habitId,
                hour = hour,
                minute = minute,
                enabled = enabled,
                repeatType = repeatType
            )

            val reminderId =
                repository.addReminder(reminder)

            if (enabled) {
                reminderScheduler.scheduleReminder(
                    reminder.copy(
                        id = reminderId.toInt()
                    )
                )
            }
        }
    }

    fun updateReminder(
        reminder: HabitReminderEntity
    ) {
        viewModelScope.launch {

            repository.updateReminder(reminder)

            if (reminder.enabled) {
                reminderScheduler.scheduleReminder(
                    reminder
                )
            } else {
                reminderScheduler.cancelReminder(
                    reminder.id
                )
            }
        }
    }

    fun deleteReminder(
        reminder: HabitReminderEntity
    ) {
        viewModelScope.launch {

            reminderScheduler.cancelReminder(
                reminder.id
            )

            repository.deleteReminder(
                reminder
            )
        }
    }

    fun toggleReminder(
        reminder: HabitReminderEntity
    ) {
        viewModelScope.launch {

            val updatedReminder =
                reminder.copy(
                    enabled = !reminder.enabled
                )

            repository.updateReminder(
                updatedReminder
            )

            if (updatedReminder.enabled) {
                reminderScheduler.scheduleReminder(
                    updatedReminder
                )
            } else {
                reminderScheduler.cancelReminder(
                    updatedReminder.id
                )
            }
        }
    }
    val allHabits = repository.getAllHabits()

    val allReminders = repository.getEnabledReminders()

    private val reminderScheduler =
        HabitReminderScheduler(
            context.applicationContext
        )
}