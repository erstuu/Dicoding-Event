package com.restugedepurnama.event.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.restugedepurnama.event.R
import com.restugedepurnama.event.data.local.entity.EventEntity
import com.restugedepurnama.event.settings.SimpleDateUtil.formatDateTime
import com.restugedepurnama.event.ui.MainActivity

class WorkerHelper (private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Event Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(event: EventEntity) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("eventId", event.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val shortEventName =
            if (event.name.length > 30) "${event.name.substring(0, 30)}..." else event.name
        val formattedDate = formatDateTime(event.beginTime)
        val notificationText = "$shortEventName starts at $formattedDate"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Upcoming Event")
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "event_reminder_channel"
        private const val NOTIFICATION_ID = 1
    }
}