package com.example.gramavaximindmatrix

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gramavaximindmatrix.ui.theme.GramaVaxiMindmatrixTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    
    private var localeState by mutableStateOf(System.currentTimeMillis())

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Notification Channel
        NotificationHelper.createNotificationChannel(this)
        
        // Schedule Vaccination Reminders
        scheduleVaccinationReminders(this)
        
        enableEdgeToEdge()
        setContent {
            // Trigger recomposition when locale changes
            val key = localeState 
            
            // Request Notification Permission for Android 13+
            RequestNotificationPermission()
            
            GramaVaxiMindmatrixTheme {
                MainApp(onLanguageChange = {
                    localeState = System.currentTimeMillis()
                    recreate()
                })
            }
        }
    }

    private fun scheduleVaccinationReminders(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<VaccinationReminderWorker>(
            24, TimeUnit.HOURS // Check once a day
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "VaccinationReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // Handle result if needed
        }

        LaunchedEffect(Unit) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MainApp(onLanguageChange: () -> Unit) {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavGraph(
            navController = navController,
            onLanguageChange = onLanguageChange
        )
    }
}
