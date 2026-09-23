package com.example.habittracker.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.habittracker.data.database.entity.HabitReminderEntity
import java.util.Calendar

class HabitReminderScheduler(
    private val context: Context
) {

    private val alarmManager =
        context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

    fun scheduleReminder(
        reminder: HabitReminderEntity
    ) {
        if (!reminder.enabled) {
            cancelReminder(reminder.id)
            return
        }

        val intent = Intent(
            context,
            HabitReminderReceiver::class.java
        ).apply {
            putExtra(
                HabitReminderReceiver.EXTRA_REMINDER_ID,
                reminder.id
            )

            putExtra(
                HabitReminderReceiver.EXTRA_HABIT_ID,
                reminder.habitId
            )
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                reminder.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelReminder(
        reminderId: Int
    ) {
        val intent = Intent(
            context,
            HabitReminderReceiver::class.java
        )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}