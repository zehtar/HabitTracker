package com.example.habittracker.data.repository

import androidx.room.withTransaction
import com.example.habittracker.data.database.HabitDatabase
import com.example.habittracker.data.database.entity.CategoryPlanEntity
import com.example.habittracker.data.database.entity.CategoryPlanHabitEntity
import com.example.habittracker.data.database.entity.CategoryPlanTargetEntity
import com.example.habittracker.data.database.entity.WeeklyPlanEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.DayOfWeek

data class WeeklyPlanTargetInput(
    val targetType: String,
    val targetValue: Int,
    val unit: String
)

data class WeeklyPlanCategoryDetails(
    val category: CategoryPlanEntity,
    val habits: List<CategoryPlanHabitEntity>,
    val targets: List<CategoryPlanTargetEntity>
)

data class WeeklyPlanDetails(
    val plan: WeeklyPlanEntity,
    val categories: List<WeeklyPlanCategoryDetails>,
    val uncategorizedHabitCount: Int
)

class WeeklyPlanRepository(
    private val database: HabitDatabase
) {
    private val weeklyPlanDao = database.weeklyPlanDao()
    private val categoryPlanDao = database.categoryPlanDao()
    private val categoryPlanTargetDao = database.categoryPlanTargetDao()
    private val categoryPlanHabitDao = database.categoryPlanHabitDao()
    private val habitDao = database.habitDao()
    private val habitEntryDao = database.habitEntryDao()
    private val weeklyPlanEntryDao = database.weeklyPlanEntryDao()

    private val categoryNames = mapOf(
        "WORK_DEVELOPMENT" to "Работа и развитие",
        "HEALTH" to "Здоровье",
        "LEARNING" to "Обучение",
        "HOBBY" to "Хобби",
        "HOME_EVERYDAY" to "Быт",
        "PERSONAL_DEVELOPMENT" to "Личное развитие"
    )

    /**
     * Returns the existing plan for this Monday or creates a new draft.
     * Habits and category names are snapshotted when the draft is created.
     */
    suspend fun getOrCreateDraft(weekStart: LocalDate): WeeklyPlanEntity {
        require(weekStart.dayOfWeek == DayOfWeek.MONDAY) {
            "Дата начала недели должна приходиться на понедельник"
        }

        return database.withTransaction {
            val weekStartString = weekStart.toString()
            val existing = weeklyPlanDao.getByWeekStart(weekStartString)
            if (existing != null) {
                snapshotExistingEntries(existing)
                return@withTransaction existing
            }

            val habits = habitDao.getAllHabitsOnce()
            val plan = WeeklyPlanEntity(
                weekStartDate = weekStartString,
                weekEndDate = weekStart.plusDays(6).toString(),
                createdAt = LocalDateTime.now().toString()
            )
            val planId = weeklyPlanDao.insert(plan).toInt()
            val createdPlan = plan.copy(id = planId)

            val categorizedHabits = habits.filter { !it.category.isNullOrBlank() }
            val groupedHabits = categorizedHabits.groupBy { it.category!! }

            groupedHabits.forEach { (categoryKey, categoryHabits) ->
                val categoryId = categoryPlanDao.insert(
                    CategoryPlanEntity(
                        weeklyPlanId = planId,
                        categoryKey = categoryKey,
                        categoryName = categoryNames[categoryKey] ?: categoryKey
                    )
                ).toInt()

                categoryPlanHabitDao.insertAll(
                    categoryHabits.map { habit ->
                        CategoryPlanHabitEntity(
                            categoryPlanId = categoryId,
                            habitId = habit.id,
                            habitName = habit.name,
                            habitType = habit.habitType.uppercase()
                        )
                    }
                )
            }

            val startDate = weekStart.toString()
            val endDate = weekStart.plusDays(6).toString()
            val snapshots = mutableListOf<com.example.habittracker.data.database.entity.WeeklyPlanEntryEntity>()

            categoryPlanDao.getForWeek(planId).forEach { category ->
                categoryPlanHabitDao.getForCategory(category.id).forEach { habitSnapshot ->
                    habitEntryDao.getEntriesForHabit(habitSnapshot.habitId)
                        .filter { it.date >= startDate && it.date <= endDate }
                        .forEach { entry ->
                            snapshots += com.example.habittracker.data.database.entity.WeeklyPlanEntryEntity(
                                weeklyPlanId = planId,
                                habitId = habitSnapshot.habitId,
                                date = entry.date,
                                habitType = habitSnapshot.habitType,
                                completed = entry.completed,
                                minutesSpent = entry.minutesSpent,
                                count = entry.count
                            )
                        }
                }
            }

            if (snapshots.isNotEmpty()) {
                weeklyPlanEntryDao.upsertEntries(snapshots)
            }

            snapshotExistingEntries(createdPlan)
            createdPlan
        }
    }

    private suspend fun snapshotExistingEntries(plan: WeeklyPlanEntity) {
        val startDate = plan.weekStartDate
        val endDate = plan.weekEndDate
        val snapshots = mutableListOf<com.example.habittracker.data.database.entity.WeeklyPlanEntryEntity>()

        categoryPlanDao.getForWeek(plan.id).forEach { category ->
            categoryPlanHabitDao.getForCategory(category.id).forEach { habitSnapshot ->
                habitEntryDao.getEntriesForHabit(habitSnapshot.habitId)
                    .filter { it.date >= startDate && it.date <= endDate }
                    .forEach { entry ->
                        snapshots += com.example.habittracker.data.database.entity.WeeklyPlanEntryEntity(
                            weeklyPlanId = plan.id,
                            habitId = habitSnapshot.habitId,
                            date = entry.date,
                            habitType = habitSnapshot.habitType,
                            completed = entry.completed,
                            minutesSpent = entry.minutesSpent,
                            count = entry.count
                        )
                    }
            }
        }

        if (snapshots.isNotEmpty()) {
            weeklyPlanEntryDao.upsertEntries(snapshots)
        }
    }

    suspend fun getPlanDetails(weeklyPlanId: Int): WeeklyPlanDetails? {
        val plan = weeklyPlanDao.getById(weeklyPlanId) ?: return null
        val categories = categoryPlanDao.getForWeek(weeklyPlanId).map { category ->
            WeeklyPlanCategoryDetails(
                category = category,
                habits = categoryPlanHabitDao.getForCategory(category.id),
                targets = categoryPlanTargetDao.getForCategory(category.id)
            )
        }

        val uncategorizedCount = habitDao.getAllHabitsOnce()
            .count { it.category.isNullOrBlank() }

        return WeeklyPlanDetails(
            plan = plan,
            categories = categories,
            uncategorizedHabitCount = uncategorizedCount
        )
    }

    suspend fun saveCategoryTargets(
        categoryPlanId: Int,
        targets: List<WeeklyPlanTargetInput>
    ) {
        require(targets.map { it.targetType.uppercase() }.distinct().size == targets.size) {
            "Для каждого типа привычек можно указать только одну цель"
        }
        targets.forEach { target ->
            require(target.targetType.uppercase() in VALID_TYPES) {
                "Неизвестный тип цели: ${target.targetType}"
            }
            require(target.targetValue > 0) {
                "Значение цели должно быть больше нуля"
            }
            require(target.unit.isNotBlank()) {
                "Необходимо указать единицу измерения"
            }
        }

        database.withTransaction {
            categoryPlanTargetDao.deleteForCategory(categoryPlanId)
            if (targets.isNotEmpty()) {
                categoryPlanTargetDao.insertAll(
                    targets.map { target ->
                        CategoryPlanTargetEntity(
                            categoryPlanId = categoryPlanId,
                            targetType = target.targetType.uppercase(),
                            targetValue = target.targetValue,
                            unit = target.unit
                        )
                    }
                )
            }
        }
    }

    suspend fun getPendingPreviousWeek(today: LocalDate = LocalDate.now()): WeeklyPlanEntity? {
        val currentMonday = today.with(DayOfWeek.MONDAY)
        val previousMonday = currentMonday.minusWeeks(1)
        return weeklyPlanDao.getByWeekStart(previousMonday.toString())
            ?.takeIf { it.status == DRAFT_STATUS }
    }

    /**
     * Calculates and stores actual values, then freezes the plan as approved.
     * TIME = total minutes, CHECK = completed entries, COUNTER = total count.
     */
    suspend fun approvePlan(
        weeklyPlanId: Int,
        note: String? = null,
        loadRating: String? = null,
        today: LocalDate = LocalDate.now()
    ) {
        require(loadRating == null || loadRating in LOAD_RATINGS) {
            "Неизвестная оценка нагрузки"
        }

        database.withTransaction {
            val plan = weeklyPlanDao.getById(weeklyPlanId)
                ?: throw IllegalArgumentException("План не найден")
            require(plan.status == DRAFT_STATUS) {
                "Можно утвердить только черновик плана"
            }
            require(LocalDate.parse(plan.weekEndDate).isBefore(today) ||
                    LocalDate.parse(plan.weekEndDate) == today) {
                "Нельзя утвердить неделю до её завершения"
            }

            snapshotExistingEntries(plan)
            val startDate = plan.weekStartDate
            val endDate = plan.weekEndDate
            val planEntries = weeklyPlanEntryDao.getForPlan(weeklyPlanId)

            categoryPlanDao.getForWeek(weeklyPlanId).forEach { category ->
                val snapshots = categoryPlanHabitDao.getForCategory(category.id)
                val habitIdsByType = snapshots
                    .groupBy { it.habitType.uppercase() }
                    .mapValues { (_, habits) -> habits.map { it.habitId }.toSet() }

                categoryPlanTargetDao.getForCategory(category.id).forEach { target ->
                    val habitIds = habitIdsByType[target.targetType.uppercase()].orEmpty()
                    val actual = planEntries.asSequence()
                        .filter { entry ->
                            entry.habitId in habitIds &&
                                entry.date >= startDate &&
                                entry.date <= endDate &&
                                entry.habitType.equals(target.targetType, ignoreCase = true)
                        }
                        .sumOf { entry ->
                            when (target.targetType.uppercase()) {
                                "TIME" -> entry.minutesSpent.coerceAtLeast(0)
                                "CHECK" -> if (entry.completed) 1 else 0
                                "COUNTER" -> entry.count.coerceAtLeast(0)
                                else -> 0
                            }
                        }

                    categoryPlanTargetDao.update(target.copy(actualValue = actual))
                }
            }

            weeklyPlanDao.update(
                plan.copy(
                    status = APPROVED_STATUS,
                    note = note?.trim()?.takeIf { it.isNotEmpty() },
                    loadRating = loadRating
                )
            )
        }
    }

    companion object {
        const val DRAFT_STATUS = "DRAFT"
        const val APPROVED_STATUS = "APPROVED"

        val VALID_TYPES = setOf("TIME", "CHECK", "COUNTER")
        val LOAD_RATINGS = setOf(
            "STRONG_SHORTAGE",
            "SHORTAGE",
            "NORMAL",
            "ABOVE_NORMAL",
            "EXCESSIVE"
        )
    }
}
