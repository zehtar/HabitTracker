package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.database.entity.WeeklyPlanEntity
import com.example.habittracker.data.repository.WeeklyPlanDetails
import com.example.habittracker.data.repository.WeeklyPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeeklyPlanHistoryUiState(
    val isLoading: Boolean = true,
    val plans: List<WeeklyPlanEntity> = emptyList(),
    val selectedPlan: WeeklyPlanDetails? = null,
    val isLoadingDetails: Boolean = false,
    val error: String? = null
)

class WeeklyPlanHistoryViewModel(
    private val repository: WeeklyPlanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeeklyPlanHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeApprovedPlans().collect { plans ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    plans = plans,
                    error = null
                )
            }
        }
    }

    fun openPlan(planId: Int) {
        if (_uiState.value.isLoadingDetails) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDetails = true, error = null)
            runCatching {
                repository.getApprovedPlanDetails(planId)
                    ?: throw IllegalStateException("Не удалось открыть сохранённый план")
            }.onSuccess { details ->
                _uiState.value = _uiState.value.copy(
                    selectedPlan = details,
                    isLoadingDetails = false,
                    error = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoadingDetails = false,
                    error = error.message ?: "Не удалось загрузить план"
                )
            }
        }
    }

    fun closePlan() {
        _uiState.value = _uiState.value.copy(selectedPlan = null)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
