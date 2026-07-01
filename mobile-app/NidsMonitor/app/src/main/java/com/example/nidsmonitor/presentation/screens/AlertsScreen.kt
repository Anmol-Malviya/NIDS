package com.example.nidsmonitor.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nidsmonitor.data.Alert
import com.example.nidsmonitor.viewmodel.NidsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlertsScreen(viewModel: NidsViewModel, onAlertClick: (Int) -> Unit) {
    val alerts by viewModel.alerts.collectAsState()

    // Group alerts by date (Extracts just the YYYY-MM-DD or date portion from the timestamp string)
    val groupedAlerts = remember(alerts) {
        alerts.groupBy { alert ->
            // Assumes your timestamp starts with a date like "2026-05-26 14:30:00"
            // taking the first 10 characters grabs just the "2026-05-26" portion.
            if (alert.timestamp.length >= 10) alert.timestamp.substring(0, 10) else "Unknown Date"
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Network Alerts",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Loop through each distinct date group
            groupedAlerts.forEach { (date, alertsForDate) ->

                // Sticky/Static Section Header for the Date Group
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Render the specific alerts belonging to this date row
                items(alertsForDate) { alert ->
                    val severityColor = when (alert.severity.uppercase()) {
                        "HIGH" -> Color(0xFFE53935)
                        "MEDIUM" -> Color(0xFFFB8C00)
                        else -> Color(0xFF43A047)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAlertClick(alert.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left-side vertical indicator line matching severity status color
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(38.dp)
                                    .background(severityColor, RoundedCornerShape(2.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = alert.attack_type,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = alert.severity.uppercase(),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = severityColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Src: ${alert.src_ip}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = alert.status,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (alert.status == "RESOLVED") Color(0xFF43A047) else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}