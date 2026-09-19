package com.example.nidsmonitor.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nidsmonitor.data.NetworkManager
import com.example.nidsmonitor.viewmodel.NidsViewModel

@Composable
fun SettingsScreen(onLogout: () -> Unit, viewModel: NidsViewModel) {
    var inputIp by remember { mutableStateOf(NetworkManager.currentIp) }
    var activeIp by remember { mutableStateOf(NetworkManager.currentIp) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    // BUG FIX #8: Derive connection status from real ViewModel health state instead of
    // hardcoding `true`. The old code ALWAYS showed "Endpoint Connected" — even when offline.
    // Now: isConnected = true only if the health data has been successfully fetched.
    val isConnected by viewModel.isConnected.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        // --- GROUP 1: NETWORK CONFIGURATION ---
        SettingsGroupTitle(icon = Icons.Default.Dns, title = "Network Configuration")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = inputIp,
                    onValueChange = { inputIp = it },
                    label = { Text("Server IP Address") },
                    placeholder = { Text("e.g. 192.168.1.103:5000") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        // NetworkManager.updateBaseUrl() now handles http:// prefix & trailing slash (BUG FIX #9)
                        NetworkManager.updateBaseUrl(inputIp)
                        activeIp = NetworkManager.currentIp
                        // Refresh data immediately after changing the server target
                        viewModel.refresh()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Target Configuration")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dynamic Status Indicator — now reflects actual server connectivity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (isConnected) Color(0xFF43A047) else Color(0xFFE53935),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) "Endpoint Connected" else "Endpoint Offline",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isConnected) Color(0xFF43A047) else Color(0xFFE53935)
                    )
                }

                Text(
                    text = "Active Server Target: $activeIp",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- GROUP 2: APP PREFERENCES ---
        SettingsGroupTitle(icon = Icons.Default.DisplaySettings, title = "Preferences")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Mode", fontWeight = FontWeight.Medium)
                        Text("Toggle system display theme", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(checked = darkMode, onCheckedChange = { darkMode = it })
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("FCM Threat Alerts", fontWeight = FontWeight.Medium)
                        Text("Receive real-time push events", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- GROUP 3: SYSTEM INFORMATION ---
        SettingsGroupTitle(icon = Icons.Default.Info, title = "System Information")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Application Version", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("v1.0.1.-ZAHID", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Console Channel", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Operator Secure Node", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- ACTIONS SECTION ---
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Terminate Console Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Reusable sub-header label for groups
@Composable
fun SettingsGroupTitle(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}