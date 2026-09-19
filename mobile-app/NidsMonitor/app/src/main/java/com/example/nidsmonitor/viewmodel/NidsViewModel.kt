package com.example.nidsmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nidsmonitor.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NidsViewModel : ViewModel() {
    private val _stats = MutableStateFlow<Stats?>(null)
    val stats: StateFlow<Stats?> = _stats

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private val _health = MutableStateFlow<Health?>(null)
    val health: StateFlow<Health?> = _health

    // Tracks whether the last fetch succeeded — used by SettingsScreen for connection status
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                // BUG FIX #6: fetchData() is now a suspend function called directly here.
                // Before: fetchData() launched its own coroutine INSIDE the polling loop,
                // causing two concurrent coroutine scopes running simultaneously every cycle.
                // That led to race conditions and stale data overwrites under slow networks.
                fetchData()
                delay(5000)
            }
        }
    }

    // BUG FIX #6: Changed from a fun that launches its own coroutine to a suspend fun.
    // It is now called sequentially from the polling loop — no more overlapping network calls.
    suspend fun fetchData() {
        try {
            val api = NetworkManager.getApi()
            _stats.value = api.getStats()
            _alerts.value = api.getAlerts()
            _health.value = api.getHealth()
            _isConnected.value = true
        } catch (e: Exception) {
            e.printStackTrace()
            _isConnected.value = false
        }
    }

    // Public wrapper so UI can trigger a one-off refresh (e.g. after settings change)
    fun refresh() {
        viewModelScope.launch {
            fetchData()
        }
    }

    fun resolveAlert(id: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                NetworkManager.getApi().resolveAlert(id)
                fetchData() // Refresh data after resolving
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // BUG FIX #11 (partial): callback fires only AFTER the API call completes,
                // ensuring navigation happens after the resolve is done.
                onComplete()
            }
        }
    }
}