package com.example.nidsmonitor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import com.example.nidsmonitor.presentation.navigation.AppNavigation
import com.example.nidsmonitor.viewmodel.NidsViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private val nidsViewModel: NidsViewModel by viewModels()

    // 🌟 NEW: Launcher to handle the user's permission response
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("🛡️ Notification authorization confirmed.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🌟 NEW: Explicitly trigger the runtime dialog prompt for target SDK 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Subscribe to the network alerts stream channel matching your Flask backend topic
        FirebaseMessaging.getInstance().subscribeToTopic("alerts")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("✅ Successfully linked to remote NIDS notification channel.")
                }
            }

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(viewModel = nidsViewModel)
                }
            }
        }
    }
}