package com.example.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.repository.WeeklyPlanCategoryDetails
import com.example.habittracker.data.repository.WeeklyPlanDetails
import com.example.habittracker.data.repository.WeeklyPlanRepository
import com.example.habittracker.data.repository.WeeklyPlanTargetInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

data class WeeklyPlanUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val details: WeeklyPlanDetails? = null,
    val error: String? = null
)

class WeeklyPlanViewModel(
    private val repository: WeeklyPlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyPlanUiState())
    val uiState = _uiState.asStateFlow()

    private var hasLoaded = false

    fun loadPlan(force: Boolean = false) {
        if (hasLoaded && !force) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            runCatching {
                val monday = LocalDate.now().with(DayOfWeek.MONDAY)
                val plan = repository.getOrCreateDraft(monday)
                repository.getPlanDetails(plan.id)
                    ?: throw IllegalStateException("Не удалось загрузить план недели")
            }.onSuccess { details ->
                hasLoaded = true
                _uiState.value = WeeklyPlanUiState(
                    isLoading = false,
                    details = details
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Не удалось загрузить план"
                )
            }
        }
    }

    fun saveCategoryTargets(
        category: WeeklyPlanCategoryDetails,
        targets: List<WeeklyPlanTargetInput>
    ) {
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                error = null
            )

            runCatching {
                repository.saveCategoryTargets(
                    categoryPlanId = category.category.id,
                    targets = targets
                )
                val planId = _uiState.value.details?.plan?.id
                    ?: throw IllegalStateException("План недели не найден")
                repository.getPlanDetails(planId)
                    ?: throw IllegalStateException("Не удалось обновить план")
            }.onSuccess { details ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    details = details,
                    error = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = error.message ?: "Не удалось сохранить цели"
                )
            }
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
