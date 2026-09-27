package com.example.habittracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.habittracker.data.database.DatabaseProvider
import com.example.habittracker.data.database.entity.HabitEntryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val reminderId =
            intent.getIntExtra(
                EXTRA_REMINDER_ID,
                -1
            )

        val habitId =
            intent.getIntExtra(
                EXTRA_HABIT_ID,
                -1
            )

        if (reminderId == -1 || habitId == -1) {
            return
        }

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val database =
                    DatabaseProvider.getDatabase(
                        context.applicationContext
                    )

                val reminder =
                    database.habitReminderDao()
                        .getReminderById(reminderId)

                if (reminder == null) {
                    return@launch
                }

                if (intent.action == ACTION_COMPLETE) {

                    markHabitCompleted(
                        context = context,
                        habitId = habitId
                    )

                    HabitNotificationManager.cancelNotification(
                        context = context,
                        reminderId = reminderId
                    )

                    context.stopService(
                        Intent(
                            context,
                            HabitReminderService::class.java
                        )
                    )

                    HabitReminderScheduler(
                        context.applicationContext
                    ).scheduleReminder(
                        reminder
                    )

                    return@launch
                }

                if (reminder.requireCompletion) {

                    val serviceIntent =
                        Intent(
                            context,
                            HabitReminderService::class.java
                        ).apply {
                            putExtra(
                                EXTRA_REMINDER_ID,
                                reminderId
                            )

                            putExtra(
                                EXTRA_HABIT_ID,
                                habitId
                            )
                        }

                    ContextCompat.startForegroundService(
                        context,
                        serviceIntent
                    )

                } else {

                    val habit =
                        database.habitDao()
                            .getHabitById(habitId)

                    HabitNotificationManager.showReminderNotification(
                        context = context,
                        reminderId = reminderId,
                        habitId = habitId,
                        habitName = habit?.name ?: "Неизвестная привычка",
                        requireCompletion = false
                    )
                }

                HabitReminderScheduler(
                    context.applicationContext
                ).scheduleReminder(
                    reminder
                )

            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun markHabitCompleted(
        context: Context,
        habitId: Int
    ) {
        val database =
            DatabaseProvider.getDatabase(
                context.applicationContext
            )

        val entryDao =
            database.habitEntryDao()

        val today =
            LocalDate.now()

        val existingEntry =
            entryDao.getEntryForDay(
                habitId = habitId,
                date = today.toString()
            )

        if (existingEntry != null) {

            entryDao.upsertEntry(
                existingEntry.copy(
                    completed = true
                )
            )

        } else {

            entryDao.upsertEntry(
                HabitEntryEntity(
                    habitId = habitId,
                    date = today.toString(),
                    completed = true,
                    minutesSpent = 0
                )
            )
        }
    }

    companion object {

        const val ACTION_COMPLETE =
            "com.example.habittracker.ACTION_COMPLETE"

        const val EXTRA_REMINDER_ID =
            "extra_reminder_id"

        const val EXTRA_HABIT_ID =
            "extra_habit_id"
    }
}