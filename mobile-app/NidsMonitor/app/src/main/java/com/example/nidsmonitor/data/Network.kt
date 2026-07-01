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

    // Explicitly update the IP and clear out the old Retrofit memory cache entirely
    fun updateBaseUrl(newIp: String) {
        currentIp = newIp
        retrofit = null // 🌟 CRUCIAL: Setting this to null kills the old connection completely
    }

    fun getApi(): ApiService {
        // If retrofit was set to null by updateBaseUrl, this IF statement triggers
        // and safely builds a new client pointing to your new IP address.
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(currentIp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }
}