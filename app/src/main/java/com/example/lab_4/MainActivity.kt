package com.example.lab_4

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: ReminderViewModel by viewModels()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ReminderScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ReminderScreen(viewModel: ReminderViewModel) {
    val context = LocalContext.current
    val text by viewModel.text.collectAsState()
    val minutes by viewModel.minutes.collectAsState()
    val status by viewModel.status.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scopeForNotify = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.scheduleReminder()
        } else {
            scopeForNotify.launch {
                snackbarHostState.showSnackbar(
                    message = "Для уведомлений необходимо разрешить доступ",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Создать напоминание", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = text,
                onValueChange = { viewModel.updateText(it) },
                label = { Text("Текст напоминания") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = minutes,
                onValueChange = { viewModel.updateMinutes(it) },
                label = { Text("Через сколько минут") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permission = Manifest.permission.POST_NOTIFICATIONS

                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            viewModel.scheduleReminder()
                        } else {
                            permissionLauncher.launch(permission)
                        }
                    } else {
                        viewModel.scheduleReminder()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Запланировать")
            }

            Button(
                onClick = { viewModel.cancelReminder() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Отменить")
            }

            if (status.isNotEmpty()) {
                Text(text = status, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}



