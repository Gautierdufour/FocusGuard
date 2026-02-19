package com.focusguard.app.workers

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.focusguard.app.AppPreferences
import com.focusguard.app.GamificationManager
import com.focusguard.app.MainActivity
import com.focusguard.app.data.StatsRepository

/**
 * Worker périodique qui envoie un bilan quotidien à 20h si les notifications sont activées.
 */
class DailyReportWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "daily_report"
        private const val ALERT_CHANNEL_ID = "app_blocker_alerts"
        private const val NOTIFICATION_ID = 42
    }

    override suspend fun doWork(): Result {
        // Vérifier si les notifications sont activées
        if (!AppPreferences.areNotificationsEnabled(context)) {
            return Result.success()
        }

        // Vérifier la permission POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return Result.success()
        }

        val statsRepository = StatsRepository.getInstance(context)
        val todayBlocks = try {
            statsRepository.getTodayBlocks()
        } catch (e: Exception) { 0 }

        val streak = GamificationManager.getCurrentStreak(context)

        val title = "FocusGuard — Bilan du jour 📊"
        val body = buildString {
            append("$todayBlocks blocage${if (todayBlocks > 1) "s" else ""} aujourd'hui · Streak ${streak}j 🔥")
            if (streak > 0 && todayBlocks == 0) {
                append("\nLance un défi pour maintenir ton streak !")
            }
        }

        sendNotification(title, body)
        return Result.success()
    }

    private fun sendNotification(title: String, body: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
