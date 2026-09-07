package com.example.habittracker.widget

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object WidgetUpdateScheduler {

    private const val WORK_NAME =
        "habit_widget_daily_update"

    fun scheduleNext(context: Context) {

        val now = LocalDateTime.now()

        val nextMidnight =
            LocalDateTime.of(
                now.toLocalDate().plusDays(1),
                LocalTime.MIDNIGHT
            )

        val delay =
            Duration.between(
                now,
                nextMidnight
            )

        val request =
            OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .setInitialDelay(
                    delay.toMillis(),
                    java.util.concurrent.TimeUnit.MILLISECONDS
                )
                .build()

        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(
                WORK_NAME,
                androidx.work.ExistingWorkPolicy.KEEP,
                request
            )
    }
}