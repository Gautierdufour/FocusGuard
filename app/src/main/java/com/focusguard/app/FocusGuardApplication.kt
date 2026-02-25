package com.focusguard.app

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.focusguard.app.workers.DailyReportWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class FocusGuardApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        scheduleDailyReport()
    }

    private fun scheduleDailyReport() {
        val delay = computeDelayUntil8pm()
        val request = PeriodicWorkRequestBuilder<DailyReportWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyReportWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    /** Calcule le délai en ms jusqu'à 20h00 (aujourd'hui ou demain si 20h déjà passée). */
    private fun computeDelayUntil8pm(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
