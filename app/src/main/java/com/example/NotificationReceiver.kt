package com.example

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val title = intent.getStringExtra("title") ?: "Task"
        val taskId = intent.getStringExtra("task_id") ?: ""

        val message = when (action) {
            "UPCOMING_TASK" -> "It's time to start: $title"
            "PAUSED_TASK" -> "Your task '$title' is paused. Ready to resume?"
            "TIMER_WARNING" -> "5 minutes left for '$title'! Wrap up or extend?"
            else -> return
        }

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "lifesort_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Fallback icon
            .setContentTitle(if (action == "TIMER_WARNING") "Almost Done!" else "LifeSort Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationId = taskId.hashCode() + (action.hashCode() % 100)
        
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}
