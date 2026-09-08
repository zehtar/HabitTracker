package com.example.habittracker.data.backup

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.example.habittracker.data.database.DatabaseProvider
import com.example.habittracker.data.database.entity.HabitEntity
import com.example.habittracker.data.database.entity.HabitEntryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


object BackupManager {

    private const val BACKUP_VERSION = 1

    suspend fun export(
        context: Context,
        uri: Uri
    ) = withContext(Dispatchers.IO) {

        val database = DatabaseProvider.getDatabase(context)

        val habits =
            database.habitDao().getAllHabitsOnce()

        val entries =
            database.habitEntryDao().getAllEntriesOnce()

        val root = JSONObject()

        root.put(
            "version",
            BACKUP_VERSION
        )

        val habitsArray = JSONArray()

        habits.forEach { habit ->

            val json = JSONObject()

            json.put("id", habit.id)
            json.put("name", habit.name)
            json.put("icon", habit.icon)
            json.put("color", habit.color)

            habitsArray.put(json)
        }

        val entriesArray = JSONArray()

        entries.forEach { entry ->

            val json = JSONObject()

            json.put("id", entry.id)
            json.put("habitId", entry.habitId)
            json.put("date", entry.date)
            json.put("completed", entry.completed)
            json.put("minutesSpent", entry.minutesSpent)

            entriesArray.put(json)
        }

        root.put(
            "habits",
            habitsArray
        )

        root.put(
            "entries",
            entriesArray
        )

        context.contentResolver
            .openOutputStream(uri)
            ?.use { outputStream ->

                outputStream.write(
                    root.toString(2)
                        .toByteArray(Charsets.UTF_8)
                )
            }
            ?: throw IllegalStateException(
                "Не удалось открыть файл для записи"
            )
    }

    suspend fun import(
        context: Context,
        uri: Uri
    ) = withContext(Dispatchers.IO) {

        val jsonText =
            context.contentResolver
                .openInputStream(uri)
                ?.use {
                    it.readBytes()
                        .toString(Charsets.UTF_8)
                }
                ?: throw IllegalStateException(
                    "Не удалось открыть файл"
                )

        val root =
            JSONObject(jsonText)

        val version =
            root.optInt(
                "version",
                -1
            )

        if (version != BACKUP_VERSION) {
            throw IllegalArgumentException(
                "Неподдерживаемая версия backup-файла"
            )
        }

        val habitsArray =
            root.optJSONArray("habits")
                ?: throw IllegalArgumentException(
                    "В backup отсутствует список привычек"
                )

        val entriesArray =
            root.optJSONArray("entries")
                ?: throw IllegalArgumentException(
                    "В backup отсутствует список записей"
                )

        val habits =
            mutableListOf<HabitEntity>()

        for (i in 0 until habitsArray.length()) {

            val json =
                habitsArray.getJSONObject(i)

            habits.add(
                HabitEntity(
                    id = json.getInt("id"),
                    name = json.getString("name"),
                    icon = json.getString("icon"),
                    color = json.getLong("color")
                )
            )
        }

        val entries =
            mutableListOf<HabitEntryEntity>()

        for (i in 0 until entriesArray.length()) {

            val json =
                entriesArray.getJSONObject(i)

            entries.add(
                HabitEntryEntity(
                    id = json.getInt("id"),
                    habitId = json.getInt("habitId"),
                    date = json.getString("date"),
                    completed = json.getBoolean("completed"),
                    minutesSpent = json.getInt("minutesSpent")
                )
            )
        }

        val habitIds =
            habits.map { it.id }.toSet()

        if (
            entries.any {
                it.habitId !in habitIds
            }
        ) {
            throw IllegalArgumentException(
                "Backup содержит записи для несуществующих привычек"
            )
        }

        val database =
            DatabaseProvider.getDatabase(context)

        database.withTransaction {

            database.habitEntryDao()
                .deleteAllEntries()

            database.habitDao()
                .deleteAllHabits()

            database.habitDao()
                .insertHabits(habits)

            database.habitEntryDao()
                .insertEntries(entries)
        }
    }

    suspend fun reset(
        context: Context
    ) = withContext(Dispatchers.IO) {

        val database =
            DatabaseProvider.getDatabase(context)

        database.withTransaction {

            database.habitEntryDao()
                .deleteAllEntries()

            database.habitDao()
                .deleteAllHabits()
        }
    }
}
