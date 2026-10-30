
package com.example.habittracker.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val icon: String,

    val color: Long,

    @ColumnInfo(defaultValue = "'TIME'")
    val habitType: String = "TIME",

    val guideline: String? = null,

    val category: String? = null,

    @ColumnInfo(defaultValue = "'IMPORTANT'")
    val priority: String = "IMPORTANT",

    @ColumnInfo(defaultValue = "NULL")
    val streakResetDate: String? = null

)
