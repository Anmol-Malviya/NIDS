package com.example.nidsmonitor.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nidsmonitor.viewmodel.NidsViewModel

@Composable
fun HealthScreen(viewModel: NidsViewModel) {
    val health by viewModel.health.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Ensures phone users can scroll if screen is small
            .padding(16.dp)
    ) {
        // Main Title Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Security Status",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Advanced NIDS",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Security Health Monitor",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Section 1: Sensor Hardware Health
        SectionHeader(icon = Icons.Default.Hardware, title = "Sensor Hardware Health")
        MetricCard {
            MetricRow("CPU Utilization", "${health?.cpu ?: "0.0"}%", "Memory Usage", "${health?.memory ?: "0.0"}%")
            Spacer(modifier = Modifier.height(8.dp))
            MetricRow("Disk Space Usage", "${health?.disk ?: "0.0"}%", "", "")
        }

        // Section 2: Network Flow Dynamics
        SectionHeader(icon = Icons.Default.NetworkCheck, title = "Network Flow Dynamics")
        MetricCard {
            MetricRow("Inbound Speed", "${health?.pps_in ?: "0"} PPS", "Outbound Speed", "${health?.pps_out ?: "0"} PPS")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            MetricRow("Total Received", "${health?.mb_recv ?: "0"} MB", "Total Sent", "${health?.mb_sent ?: "0"} MB")
        }

        // Section 3: Traffic Integrity & Delay
        SectionHeader(icon = Icons.Default.BugReport, title = "Traffic Integrity & Delay")
        MetricCard {
            MetricRow("Diagnostic Latency", health?.latency ?: "N/A", "", "")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            MetricRow("Inbound Errors", "${health?.errors ?: "0"}", "Inbound Drops", "${health?.drops ?: "0"}")
        }

        // Section 4: Firewall & Socket Inspection
        SectionHeader(icon = Icons.Default.DeviceHub, title = "Firewall & Socket Inspection")
        MetricCard {
            MetricRow("ESTABLISHED (Live)", "${health?.established ?: "0"}", "SYN_RECV (DoS Aim)", "${health?.syn_recv ?: "0"}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            MetricRow("TIME_WAIT (Scans)", "${health?.time_wait ?: "0"}", "SYN_SENT (Outbound)", "${health?.syn_sent ?: "0"}")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Reusable Section Header Component
@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 1.sp
        )
    }
}

// Reusable Card Wrapper Component
@Composable
fun MetricCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// Helper layout helper to display two items side-by-side cleanly on phone screens
@Composable
fun MetricRow(label1: String, value1: String, label2: String, value2: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label1, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value1, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        if (label2.isNotEmpty()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label2, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value2, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthScreenPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Advanced NIDS", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                    Text("Security Health Monitor", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }

            SectionHeader(icon = Icons.Default.Hardware, title = "Sensor Hardware Health")
            MetricCard {
                MetricRow("CPU Utilization", "22.2%", "Memory Usage", "85.9%")
                Spacer(modifier = Modifier.height(8.dp))
                MetricRow("Disk Space Usage", "67.7%", "", "")
            }

            SectionHeader(icon = Icons.Default.NetworkCheck, title = "Network Flow Dynamics")
            MetricCard {
                MetricRow("Inbound Speed", "1 PPS", "Outbound Speed", "1.3 PPS")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                MetricRow("Total Received", "587.06 MB", "Total Sent", "122.85 MB")
            }
        }
    }
}