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

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                fetchData()
                delay(5000) // Polling interval
            }
        }
    }

    // 🌟 CHANGED: Always fetches a fresh API client reference inside the block
    fun fetchData() {
        viewModelScope.launch {
            try {
                // Grabbing getApi() directly here ensures it reads the newly updated IP instantly
                val api = NetworkManager.getApi()
                _stats.value = api.getStats()
                _alerts.value = api.getAlerts()
                _health.value = api.getHealth()
            } catch (e: Exception) {
                e.printStackTrace() // Prevents app crashes if the new IP is temporarily offline
            }
        }
    }

    fun resolveAlert(id: Int) {
        viewModelScope.launch {
            try {
                NetworkManager.getApi().resolveAlert(id)
                fetchData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}