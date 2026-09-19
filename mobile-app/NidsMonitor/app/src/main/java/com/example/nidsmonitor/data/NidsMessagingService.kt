package com.example.nidsmonitor.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.nidsmonitor.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class NidsMessagingService : FirebaseMessagingService() {

    // Useful for debugging: this token needs to be sent to your Flask server
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token generated: $token")
        // TODO: Send this token to your Flask backend so it knows which device to alert
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val deepLinkUriString = remoteMessage.data["deep_link_uri"]
        val title = remoteMessage.notification?.title ?: "NIDS Alert"
        val body = remoteMessage.notification?.body ?: "Threat activity detected."

        showNotification(title, body, deepLinkUriString)
    }

    private fun showNotification(title: String, body: String, deepLinkUrl: String?) {
        val channelId = "nids_alerts_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "NIDS Threat Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // BUG FIX #7: getLaunchIntentForPackage() can return null on some devices/launchers.
        // The old code passed a nullable Intent directly to PendingIntent.getActivity(),
        // causing a NullPointerException crash.
        // Fix: Use a safe fallback to an explicit Intent pointing at MainActivity.
        val intent = if (!deepLinkUrl.isNullOrEmpty()) {
            Intent(Intent.ACTION_VIEW, deepLinkUrl.toUri()).apply {
                setPackage(packageName)
            }
        } else {
            // Safe fallback: explicit Intent to MainActivity — never returns null
            packageManager.getLaunchIntentForPackage(packageName)
                ?: Intent(this, MainActivity::class.java)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }
}
