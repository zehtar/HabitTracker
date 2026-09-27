package com.example.habittracker.notification

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.habittracker.R
import com.example.habittracker.data.database.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class HabitReminderService : Service() {

    private val serviceScope =
        CoroutineScope(Dispatchers.Default)

    private var notificationJob: Job? = null

    private var reminderId: Int = -1
    private var habitId: Int = -1

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        reminderId =
            intent.getIntExtra(
                HabitReminderReceiver.EXTRA_REMINDER_ID,
                -1
            )

        habitId =
            intent.getIntExtra(
                HabitReminderReceiver.EXTRA_HABIT_ID,
                -1
            )

        if (reminderId == -1 || habitId == -1) {
            stopSelf()
            return START_NOT_STICKY
        }

        startReminder()

        return START_NOT_STICKY
    }

    private fun startReminder() {

        if (notificationJob?.isActive == true) {
            return
        }

        val notification =
            createNotification()

        ServiceCompat.startForeground(
            this,
            reminderId,
            notification,
            android.content.pm.ServiceInfo
                .FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        )

        notificationJob =
            serviceScope.launch {

                val database =
                    DatabaseProvider.getDatabase(
                        applicationContext
                    )

                val habit =
                    database.habitDao()
                        .getHabitById(habitId)

                val habitName =
                    habit?.name
                        ?: "Неизвестная привычка"

                while (isActive) {

                    HabitNotificationManager.showReminderNotification(
                        context = this@HabitReminderService,
                        reminderId = reminderId,
                        habitId = habitId,
                        habitName = habitName,
                        requireCompletion = true
                    )

                    delay(5_000)
                }
            }
    }
    private fun createNotification(): Notification {

        val completeIntent =
            Intent(
                this,
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
            android.app.PendingIntent.getBroadcast(
                this,
                reminderId + 100000,
                completeIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                        android.app.PendingIntent.FLAG_IMMUTABLE
            )

        return NotificationCompat.Builder(
            this,
            HabitNotificationManager.CHANNEL_ID
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
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(
                0,
                "Выполнено",
                completePendingIntent
            )
            .build()
    }

    override fun onDestroy() {

        notificationJob?.cancel()
        notificationJob = null

        serviceScope.cancel()

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {
        return null
    }
}