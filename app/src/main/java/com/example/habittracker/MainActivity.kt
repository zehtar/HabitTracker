package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.habittracker.ui.navigation.HabitNavigation
import com.example.habittracker.ui.theme.HabitTrackerTheme
import com.example.habittracker.widget.WidgetUpdateScheduler

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WidgetUpdateScheduler.scheduleNext(this)

        enableEdgeToEdge()

        setContent {

            HabitTrackerTheme {

                val habitId = intent.getIntExtra(
                    "habit_id",
                    -1
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    HabitNavigation(
                        modifier = Modifier.padding(innerPadding),
                        initialHabitId = habitId
                    )

                }

            }

        }

    }

}

