package com.example.nidsmonitor.data

// Maps to /api/stats response
data class Stats(
    val total_alerts: Int,
    val high_alerts: Int,
    val medium_alerts: Int,
    val low_alerts: Int
)

// Maps to /api/alerts response array
data class Alert(
    val id: Int,
    val attack_type: String,
    val prediction: Int,
    val severity: String,
    val src_ip: String,
    val dst_ip: String,
    val source_port: Int,
    val destination_port: Int,
    val protocol: Int,
    val flow_duration: Double,
    val status: String,
    val timestamp: String
)

// Maps to /api/health response (UPDATED WITH ALL FIELDS)
data class Health(
    val cpu: Double,
    val memory: Double,
    val disk: Double,
    val pps_in: Double,
    val pps_out: Double,
    val latency: String,
    val drops: Int,
    val errors: Int,
    val established: Int,
    val mb_recv: Double,
    val mb_sent: Double,
    val syn_recv: Int,
    val syn_sent: Int,
    val time_wait: Int
)