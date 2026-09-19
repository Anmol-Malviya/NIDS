package com.example.nidsmonitor.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.nidsmonitor.presentation.screens.*
import com.example.nidsmonitor.viewmodel.NidsViewModel

// Sealed class holds distinct selected vs unselected icon resource paths
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector?,
    val unselectedIcon: ImageVector?
) {
    object Login : Screen("login", "Login", null, null)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Home, Icons.Outlined.Home)
    object Alerts : Screen("alerts", "Alerts", Icons.Filled.NotificationsActive, Icons.Outlined.Notifications)
    object AlertDetail : Screen("alert_detail/{alertId}", "Detail", null, null)
    object Health : Screen("health", "Health", Icons.Filled.Info, Icons.Outlined.Info)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun AppNavigation(viewModel: NidsViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Screen.Login.route && currentRoute?.startsWith("alert_detail") == false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(Screen.Dashboard, Screen.Alerts, Screen.Health, Screen.Settings)
                    items.forEach { screen ->
                        val isSelected = currentRoute == screen.route

                        NavigationBarItem(
                            icon = {
                                // BUG FIX #5: Replaced unsafe !! force-unwrap with safe null-checks.
                                // Screen.Login and Screen.AlertDetail have null icons — if they ever
                                // ended up in the list, !! would crash with NullPointerException.
                                // Now uses a safe fallback icon instead.
                                val icon: ImageVector = if (isSelected) {
                                    screen.selectedIcon ?: Icons.Filled.Home
                                } else {
                                    screen.unselectedIcon ?: Icons.Outlined.Home
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        // BUG FIX #13: Replaced deprecated integer-based popUpTo(startDestinationId)
                                        // with the route-string API required by Navigation 2.8+/2.9.x.
                                        popUpTo(Screen.Dashboard.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }

            composable(Screen.Dashboard.route) { DashboardScreen(viewModel) }

            composable(Screen.Alerts.route) {
                AlertsScreen(viewModel, onAlertClick = { id -> navController.navigate("alert_detail/$id") })
            }

            composable(
                route = Screen.AlertDetail.route,
                arguments = listOf(navArgument("alertId") { type = NavType.IntType }),
                deepLinks = listOf(navDeepLink { uriPattern = "nidsapp://alert/{alertId}" })
            ) { backStackEntry ->
                val alertId = backStackEntry.arguments?.getInt("alertId") ?: 0
                AlertDetailScreen(alertId, viewModel, onBack = { navController.popBackStack() })
            }

            composable(Screen.Health.route) { HealthScreen(viewModel) }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onLogout = {
                        navController.navigate(Screen.Login.route) { popUpTo(0) }
                    }
                )
            }
        }
    }
}