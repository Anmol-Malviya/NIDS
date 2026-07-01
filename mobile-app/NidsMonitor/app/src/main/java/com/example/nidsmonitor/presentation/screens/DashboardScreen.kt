package com.example.nidsmonitor.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.NewReleases
// Add this instead:
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nidsmonitor.viewmodel.NidsViewModel

@Composable
fun DashboardScreen(viewModel: NidsViewModel) {
    // This constantly listens to viewmodel flows and updates your UI whenever data changes or refreshes!
    val stats by viewModel.stats.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Main Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Report, // Changed from ShieldAlert
                contentDescription = "NIDS Dashboard",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Threat Intelligence",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Dashboard Overview",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Total Threats Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TOTAL THREATS DETECTED",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "${stats?.total_alerts ?: "0"}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Icon(
                    imageVector = Icons.Default.GppBad,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        // Severity Breakdown Row Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeverityMiniCard(
                modifier = Modifier.weight(1f),
                title = "High",
                count = stats?.high_alerts ?: 0,
                icon = Icons.Default.NewReleases,
                color = Color(0xFFE53935) // Deep Red
            )
            SeverityMiniCard(
                modifier = Modifier.weight(1f),
                title = "Medium",
                count = stats?.medium_alerts ?: 0,
                icon = Icons.Default.Warning,
                color = Color(0xFFFB8C00) // Orange
            )
            SeverityMiniCard(
                modifier = Modifier.weight(1f),
                title = "Low",
                count = stats?.low_alerts ?: 0,
                icon = Icons.Default.Assessment,
                color = Color(0xFF43A047) // Green
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Chart Title
        Text(
            text = "Threat Distribution History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Native Visual Metrics Bar Chart
        ThreatBarChart(
            high = stats?.high_alerts ?: 0,
            medium = stats?.medium_alerts ?: 0,
            low = stats?.low_alerts ?: 0
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Reusable component for the Tier levels (High, Medium, Low)
@Composable
fun SeverityMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "$count", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }
    }
}

// Native Jetpack Compose Bar Graphic Rendering
@Composable
fun ThreatBarChart(high: Int, medium: Int, low: Int) {
    val maxVal = maxOf(high, medium, low, 1).toFloat() // Avoid division by zero

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ChartBarItem(label = "Critical/High Risk", count = high, max = maxVal, color = Color(0xFFE53935))
            Spacer(modifier = Modifier.height(14.dp))
            ChartBarItem(label = "Medium Severity", count = medium, max = maxVal, color = Color(0xFFFB8C00))
            Spacer(modifier = Modifier.height(14.dp))
            ChartBarItem(label = "Low Warning", count = low, max = maxVal, color = Color(0xFF43A047))
        }
    }
}

@Composable
fun ChartBarItem(label: String, count: Int, max: Float, color: Color) {
    val progressTarget = count / max
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 800),
        label = "barAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = "$count instances", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        // Render stylized graphic fill track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = if (animatedProgress > 0f) animatedProgress else 0.02f) // Minimal line baseline visible
                    .background(color, RoundedCornerShape(6.dp))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                Icon(imageVector = Icons.Default.Report, contentDescription = null, tint = Color.Red, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Threat Intelligence", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                    Text("Dashboard Overview", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }

            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TOTAL THREATS DETECTED", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                        Text("142", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                    Icon(imageVector = Icons.Default.GppBad, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(56.dp))
                }
            }

            ThreatBarChart(high = 84, medium = 42, low = 16)
        }
    }
}