package com.example.nidsmonitor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import com.example.nidsmonitor.presentation.navigation.AppNavigation
import com.example.nidsmonitor.ui.theme.NidsMonitorTheme
import com.example.nidsmonitor.viewmodel.NidsViewModel

class MainActivity : ComponentActivity() {
    private val nidsViewModel: NidsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("🛡️ Notification authorization confirmed.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Trigger runtime notification permission dialog for API 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Subscribe to FCM alerts topic — wrapped in try-catch for builds without real google-services.json
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance()
                .subscribeToTopic("alerts")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("✅ Successfully linked to remote NIDS notification channel.")
                    } else {
                        println("⚠️ FCM subscription skipped — no valid Firebase config.")
                    }
                }
        } catch (e: Exception) {
            println("⚠️ Firebase not configured: ${e.message}")
        }

        setContent {
            // BUG FIX #4: Use NidsMonitorTheme instead of plain MaterialTheme.
            // The old code bypassed the custom colour scheme, dark mode support, and typography.
            NidsMonitorTheme {
                Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.background) {
                    AppNavigation(viewModel = nidsViewModel)
                }
            }
        }
    }
}