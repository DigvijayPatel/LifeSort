package com.example

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object NotificationHelper {
    private const val CHANNEL_ID = "lifesort_channel"
    private const val CHANNEL_NAME = "LifeSort Notifications"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Task reminders and timer alerts"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleTaskNotification(context: Context, taskId: String, title: String, timeInMillis: Long) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "UPCOMING_TASK"
            putExtra("task_id", taskId)
            putExtra("title", title)
        }
        scheduleAlarm(context, intent, timeInMillis, taskId.hashCode())
    }

    fun schedulePausedTaskReminder(context: Context, taskId: String, title: String, timeInMillis: Long) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "PAUSED_TASK"
            putExtra("task_id", taskId)
            putExtra("title", title)
        }
        scheduleAlarm(context, intent, timeInMillis, taskId.hashCode() + 1)
    }

    fun scheduleTimer5MinWarning(context: Context, taskId: String, title: String, timeInMillis: Long) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "TIMER_WARNING"
            putExtra("task_id", taskId)
            putExtra("title", title)
        }
        scheduleAlarm(context, intent, timeInMillis, taskId.hashCode() + 2)
    }

    fun cancelTimer5MinWarning(context: Context, taskId: String) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "TIMER_WARNING"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode() + 2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
    
    fun cancelPausedTaskReminder(context: Context, taskId: String) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "PAUSED_TASK"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode() + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleAlarm(context: Context, intent: Intent, timeInMillis: Long, requestCode: Int) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
    }
}
