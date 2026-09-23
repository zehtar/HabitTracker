package com.example.habittracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

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

        HabitNotificationManager.showReminderNotification(
            context = context,
            reminderId = reminderId,
            habitId = habitId
        )
    }

    companion object {

        const val EXTRA_REMINDER_ID =
            "extra_reminder_id"

        const val EXTRA_HABIT_ID =
            "extra_habit_id"
    }
}