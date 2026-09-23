package com.example.habittracker.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.habittracker.R

object HabitNotificationManager {

    private const val CHANNEL_ID =
        "habit_reminders"

    private const val CHANNEL_NAME =
        "Напоминания о привычках"

    fun createNotificationChannel(
        context: Context
    ) {
        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description =
                "Напоминания о выполнении привычек"
        }

        manager.createNotificationChannel(channel)
    }

    fun showReminderNotification(
        context: Context,
        reminderId: Int,
        habitId: Int
    ) {
        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(
                    "Напоминание о привычке"
                )
                .setContentText(
                    "Не забудьте выполнить привычку"
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.notify(
            reminderId,
            notification
        )
    }
}