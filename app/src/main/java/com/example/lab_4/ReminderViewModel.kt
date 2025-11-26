package com.example.lab_4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)
    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()
    private val _minutes = MutableStateFlow("")
    val minutes = _minutes.asStateFlow()
    private val _status = MutableStateFlow("")
    val status = _status.asStateFlow()

    fun updateText(newText: String) {
        _text.value = newText
    }

    fun updateMinutes(newMinutes: String) {
        _minutes.value = newMinutes
    }

    fun scheduleReminder() {
        val min = _minutes.value.toLongOrNull()
        val reminderText = _text.value

        if (min != null && reminderText.isNotBlank()) {
            val data = workDataOf("ТЕКСТ" to reminderText)

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(min, TimeUnit.MINUTES)
                .setInputData(data)
                .build()

            workManager.enqueueUniqueWork(
                "воркер",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

            _status.value = "Напоминание запланировано через $min мин."
        } else {
            _status.value = "Заполните поля корректно"
        }
    }

    fun cancelReminder() {
        workManager.cancelUniqueWork("воркер")
        _status.value = "Напоминание отменено"
    }
}

