package com.example.habittracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.habittracker.R

object HabitNotificationManager {

    const val CHANNEL_ID =
        "habit_reminders"

    private const val CHANNEL_NAME =
        "Напоминания о привычках"
    const val REQUIRED_CHANNEL_ID =
        "habit_required_reminders"
    fun createNotificationChannel(
        context: Context
    ) {
        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val normalChannel = NotificationChannel(
            CHANNEL_ID,
            "Напоминания о привычках",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description =
                "Обычные напоминания о выполнении привычек"
        }

        val requiredChannel = NotificationChannel(
            REQUIRED_CHANNEL_ID,
            "Обязательные напоминания",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description =
                "Напоминания, требующие выполнения привычки"
        }

        manager.createNotificationChannel(normalChannel)
        manager.createNotificationChannel(requiredChannel)
    }

    fun showReminderNotification(
        context: Context,
        reminderId: Int,
        habitId: Int,
        habitName: String,
        requireCompletion: Boolean
    ) {
        val channelId =
            if (requireCompletion) {
                REQUIRED_CHANNEL_ID
            } else {
                CHANNEL_ID
            }

        val builder =
            NotificationCompat.Builder(
                context,
                channelId
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(
                    "Напоминание о привычке"
                )
                .setContentText(
                    habitName
                )
                .setPriority(
                    if (requireCompletion) {
                        NotificationCompat.PRIORITY_LOW
                    } else {
                        NotificationCompat.PRIORITY_HIGH
                    }
                )
                .setAutoCancel(
                    !requireCompletion
                )

        if (requireCompletion) {

            val completeIntent =
                Intent(
                    context,
                    HabitReminderReceiver::class.java
                ).apply {

                    action =
                        HabitReminderReceiver.ACTION_COMPLETE

                    putExtra(
                        HabitReminderReceiver.EXTRA_REMINDER_ID,
                        reminderId
                    )

                    putExtra(
                        HabitReminderReceiver.EXTRA_HABIT_ID,
                        habitId
                    )
                }

            val completePendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    reminderId + 100000,
                    completeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

            builder
                .setOngoing(true)
                .addAction(
                    0,
                    "Выполнено",
                    completePendingIntent
                )
        }

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        android.util.Log.d(
            "HabitNotification",
            "SHOW: reminder=$reminderId, habit=$habitName, " +
                    "required=$requireCompletion, channel=$channelId"
        )

        android.util.Log.d(
            "HabitNotification",
            "CHANNEL: id=$channelId, importance=" +
                    manager.getNotificationChannel(channelId)?.importance
        )

        manager.notify(
            reminderId,
            builder.build()
        )
    }

    fun cancelNotification(
        context: Context,
        reminderId: Int
    ) {
        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.cancel(reminderId)
    }
}