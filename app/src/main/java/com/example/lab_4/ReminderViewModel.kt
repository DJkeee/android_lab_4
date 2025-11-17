package com.example.lab_4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.os.Build


class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmHelper = AlarmHelper(application)
    private val notificationHelper = NotificationHelper(application)

    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _minutes = MutableStateFlow("")
    val minutes = _minutes.asStateFlow()

    private val _status = MutableStateFlow("")
    val status = _status.asStateFlow()

    init {
        notificationHelper.createNotificationChannel()
    }

    fun scheduleReminder() {
        val reminderText = _text.value.trim()
        val delayMinutes = _minutes.value.trim().toIntOrNull()

        if (delayMinutes == null || reminderText.isBlank()) {
            _status.value = "❌ Заполните поля корректно"
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmHelper.canScheduleExactAlarms()) {
            _status.value = "⚠️ Требуется разрешение на будильник"
            alarmHelper.openAlarmSettings()
            return
        }

        alarmHelper.scheduleReminder(reminderText, delayMinutes)
        _status.value = "✅ Напоминание через $delayMinutes мин"
    }

    fun cancelReminder() {
        alarmHelper.cancelReminder()
        _status.value = "Напоминание отменено"
    }

    fun updateText(newText: String) {
        _text.value = newText
    }

    fun updateMinutes(newMinutes: String) {
        _minutes.value = newMinutes.filter { it.isDigit() }
    }
}
