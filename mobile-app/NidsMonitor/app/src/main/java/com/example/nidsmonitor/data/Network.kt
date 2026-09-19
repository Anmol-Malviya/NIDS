package com.example.nidsmonitor.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/stats")
    suspend fun getStats(): Stats

    @GET("api/alerts")
    suspend fun getAlerts(): List<Alert>

    @GET("api/health")
    suspend fun getHealth(): Health

    @GET("api/resolve/{id}")
    suspend fun resolveAlert(@Path("id") id: Int): Map<String, String>
}

object NetworkManager {
    var currentIp: String = "http://192.168.1.103:5000/"
    private var retrofit: Retrofit? = null

    // BUG FIX #9: Sanitize the URL before storing it.
    // Ensures http:// prefix is present and a trailing slash exists.
    // Without this, Retrofit throws IllegalArgumentException if the user enters a bare IP.
    fun updateBaseUrl(newIp: String) {
        var sanitized = newIp.trim()
        // Prepend http:// if no scheme is present
        if (!sanitized.startsWith("http://") && !sanitized.startsWith("https://")) {
            sanitized = "http://$sanitized"
        }
        // Ensure trailing slash (required by Retrofit)
        if (!sanitized.endsWith("/")) {
            sanitized = "$sanitized/"
        }
        currentIp = sanitized
        retrofit = null // Clears old connection so getApi() rebuilds with new URL
    }

    fun getApi(): ApiService {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(currentIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }
}