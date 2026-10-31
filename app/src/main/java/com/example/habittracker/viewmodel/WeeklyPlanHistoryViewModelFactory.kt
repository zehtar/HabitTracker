package com.example.habittracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.data.database.DatabaseProvider
import com.example.habittracker.data.repository.WeeklyPlanRepository

class WeeklyPlanHistoryViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = DatabaseProvider.getDatabase(context)
        val repository = WeeklyPlanRepository(database)

        @Suppress("UNCHECKED_CAST")
        return WeeklyPlanHistoryViewModel(repository) as T
    }
}
