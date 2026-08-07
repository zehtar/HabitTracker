package com.example.habittracker.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WidgetUpdater {

    suspend fun update(context: Context) {

        val appContext = context.applicationContext

        val manager =
            GlanceAppWidgetManager(appContext)

        val glanceIds =
            manager.getGlanceIds(
                HabitWidget::class.java
            )

        withContext(Dispatchers.Default) {

            glanceIds.forEach { glanceId ->

                HabitWidget().update(
                    appContext,
                    glanceId
                )
            }
        }
    }
}