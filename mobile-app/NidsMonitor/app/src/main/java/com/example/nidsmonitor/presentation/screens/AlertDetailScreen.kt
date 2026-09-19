package com.example.nidsmonitor.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nidsmonitor.viewmodel.NidsViewModel

@Composable
fun AlertDetailScreen(alertId: Int, viewModel: NidsViewModel, onBack: () -> Unit) {
    val alerts by viewModel.alerts.collectAsState()
    val alert = alerts.find { it.id == alertId }

    // BUG FIX #11: Track resolving state to prevent navigating away before API completes.
    // The old code called resolveAlert() and onBack() in the same lambda, causing a race
    // condition where navigation happened before the async resolve finished.
    var isResolving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Navigation Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Alerts"
                )
            }
            Text(
                text = "Incident Investigation",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (alert != null) {
            // Main Heading Row with ID and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alert #${alert.id}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                // Status Badge
                val isResolved = alert.status == "RESOLVED"
                Surface(
                    color = if (isResolved) Color(0xFF43A047).copy(alpha = 0.15f) else Color(0xFFE53935).copy(alpha = 0.15f),
                    contentColor = if (isResolved) Color(0xFF43A047) else Color(0xFFE53935),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = alert.status,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Threat Banner
            val severityColor = when (alert.severity.uppercase()) {
                "HIGH" -> Color(0xFFE53935)
                "MEDIUM" -> Color(0xFFFB8C00)
                else -> Color(0xFF43A047)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = severityColor.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = severityColor,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = alert.attack_type,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = severityColor
                        )
                        Text(
                            text = "${alert.severity} Severity Threat Vector",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            DetailSectionTitle(icon = Icons.Default.NetworkCheck, title = "Network Packet Details")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailGridRow("Source IP", alert.src_ip, "Source Port", "${alert.source_port}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    DetailGridRow("Destination IP", alert.dst_ip, "Dest Port", "${alert.destination_port}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    DetailGridRow("Protocol Layer", "IANA ID: ${alert.protocol}", "Flow Duration", "${alert.flow_duration} ms")
                }
            }

            DetailSectionTitle(icon = Icons.Default.Gavel, title = "AI Engine Diagnostics & Logs")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailGridRow("Model Prediction", "Class Asset ${alert.prediction}", "Timestamp", alert.timestamp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BUG FIX #11: Resolve button now shows a loading state and only calls onBack()
            // AFTER the API resolveAlert() completes via the onComplete callback.
            // The old code called onBack() immediately in the same click lambda, causing
            // navigation before the async operation finished.
            if (alert.status != "RESOLVED") {
                Button(
                    onClick = {
                        if (!isResolving) {
                            isResolving = true
                            viewModel.resolveAlert(alert.id) {
                                onBack() // Navigate ONLY after resolve completes
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isResolving
                ) {
                    if (isResolving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resolving...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark Incident As Resolved", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Error View Fallback
            Box(
                modifier = Modifier.fillMaxWidth().padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Alert target profile not found.", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// Reusable Section Label Helper Component
@Composable
fun DetailSectionTitle(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline,
            letterSpacing = 0.5.sp
        )
    }
}

// Side-by-side technical metadata grid helper
@Composable
fun DetailGridRow(label1: String, value1: String, label2: String, value2: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(text = label1, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            Text(text = value1, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label2, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            Text(text = value2, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}