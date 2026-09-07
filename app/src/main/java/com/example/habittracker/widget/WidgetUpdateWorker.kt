package com.example.habittracker.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WidgetUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    override suspend fun doWork(): Result {

        HabitWidget().updateAll(
            applicationContext
        )

        WidgetUpdateScheduler.scheduleNext(
            applicationContext
        )

        return Result.success()
    }
}