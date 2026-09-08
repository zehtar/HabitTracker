package com.example.habittracker.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.habittracker.data.backup.BackupManager
import com.example.habittracker.widget.WidgetUpdater
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var showResetDialog by remember {
        mutableStateOf(false)
    }

    var showImportDialog by remember {
        mutableStateOf(false)
    }

    var pendingImportUri by remember {
        mutableStateOf<android.net.Uri?>(null)
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument(
                "application/octet-stream"
            )
        ) { uri ->

            if (uri != null) {

                scope.launch {

                    try {

                        BackupManager.export(
                            context,
                            uri
                        )

                        Toast.makeText(
                            context,
                            "Данные экспортированы",
                            Toast.LENGTH_SHORT
                        ).show()

                    } catch (e: Exception) {

                        Toast.makeText(
                            context,
                            "Ошибка экспорта: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    val importLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                pendingImportUri = uri
                showImportDialog = true
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        Text(
            text = "Настройки",
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 20.dp
            )
        )

        SettingsSection(
            title = "Оформление"
        )

        SettingsItem(
            title = "Тема",
            subtitle = "Системная",
            enabled = false
        )

        SettingsItem(
            title = "Цвет приложения",
            subtitle = "По умолчанию",
            enabled = false
        )

        HorizontalDivider(
            modifier = Modifier.padding(
                vertical = 8.dp
            )
        )

        SettingsSection(
            title = "Привычки"
        )

        SettingsItem(
            title = "Настройки привычек",
            subtitle = "Пока недоступно",
            enabled = false
        )

        SettingsItem(
            title = "Напоминания",
            subtitle = "Пока недоступно",
            enabled = false
        )

        HorizontalDivider(
            modifier = Modifier.padding(
                vertical = 8.dp
            )
        )

        SettingsSection(
            title = "Данные"
        )

        SettingsItem(
            title = "Экспорт данных",
            subtitle = "Создать резервную копию",
            onClick = {

                exportLauncher.launch(
                    "habittracker_backup.habit"
                )
            }
        )

        SettingsItem(
            title = "Импорт данных",
            subtitle = "Восстановить данные из резервной копии",
            onClick = {

                importLauncher.launch(
                    arrayOf(
                        "application/octet-stream",
                        "application/json",
                        "*/*"
                    )
                )
            }
        )

        SettingsItem(
            title = "Сбросить все данные",
            subtitle = "Удалить все привычки и историю",
            onClick = {
                showResetDialog = true
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(
                vertical = 8.dp
            )
        )

        SettingsSection(
            title = "О приложении"
        )

        SettingsItem(
            title = "Версия",
            subtitle = "1.0.0",
            enabled = false
        )

        SettingsItem(
            title = "GitHub",
            subtitle = "HabitTracker",
            enabled = false
        )
    }

    if (showImportDialog) {

        AlertDialog(
            onDismissRequest = {
                showImportDialog = false
                pendingImportUri = null
            },

            title = {
                Text("Импорт данных")
            },

            text = {
                Text(
                    "Импорт полностью заменит текущие данные приложения. " +
                            "Текущие привычки и история будут удалены."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val uri =
                            pendingImportUri

                        showImportDialog = false
                        pendingImportUri = null

                        if (uri != null) {

                            scope.launch {

                                try {

                                    BackupManager.import(
                                        context,
                                        uri
                                    )

                                    WidgetUpdater.update(
                                        context
                                    )

                                    Toast.makeText(
                                        context,
                                        "Данные восстановлены",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } catch (e: Exception) {

                                    Toast.makeText(
                                        context,
                                        "Ошибка импорта: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                ) {
                    Text("Импортировать")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        showImportDialog = false
                        pendingImportUri = null
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showResetDialog) {

        AlertDialog(
            onDismissRequest = {
                showResetDialog = false
            },

            title = {
                Text("Сбросить данные?")
            },

            text = {
                Text(
                    "Все привычки и история их выполнения будут удалены. " +
                            "Это действие нельзя отменить."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showResetDialog = false

                        scope.launch {

                            try {

                                BackupManager.reset(
                                    context
                                )

                                WidgetUpdater.update(
                                    context
                                )

                                Toast.makeText(
                                    context,
                                    "Все данные удалены",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    "Ошибка сброса: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                ) {
                    Text("Удалить")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showResetDialog = false
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String
) {

    Text(
        text = title,
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    )
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {

    ListItem(
        headlineContent = {
            Text(title)
        },

        supportingContent = subtitle?.let {
            {
                Text(it)
            }
        },

        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (enabled && onClick != null) {
                    Modifier.clickable(
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
    )
}