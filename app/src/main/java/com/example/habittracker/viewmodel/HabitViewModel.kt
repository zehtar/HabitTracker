package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class HabitViewModel(
    private val repository: HabitRepository
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

        }
    }
}