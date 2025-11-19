package com.example.lab_4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Сигнал получен")
        try {
            val text = intent.getStringExtra("text") ?: throw Exception("Текст пустой")
            NotificationHelper(context).showNotification(text)
            Log.d("AlarmReceiver", "Уведомление показано")
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "ОШИБКА", e)
        }
    }
}