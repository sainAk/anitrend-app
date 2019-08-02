package com.mxt.anitrend.util


import java.util.concurrent.TimeUnit
import android.content.Context
import androidx.work.*
import com.mxt.anitrend.service.JobDispatcherService

/**
 * Created by Maxwell on 12/4/2016.
 * Schedules future services via job dispatcher
 */
object JobSchedulerUtil {

    private val constraints = Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

    /**
     * Schedules a new job service or replaces the existing job if one exists.
     * @param context any valid application context
     */
    fun scheduleJob(context: Context) {
        val settings = Settings(context)
        if (settings.isAuthenticated && settings.isNotificationEnabled) {
            val periodicWorkRequest = PeriodicWorkRequest.Builder(
                JobDispatcherService::class.java,
                settings.syncTime.toLong(),
                TimeUnit.MINUTES
            ).setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                5,
                TimeUnit.MINUTES
            )
            .addTag(KeyUtil.WorkNotificationTag)
            .setConstraints(constraints)
            .build()

            WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(
                        KeyUtil.WorkNotificationId,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        periodicWorkRequest
                    )
        }
    }

    /**
     * Cancels any scheduled jobs.
     */
    fun cancelJob(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(KeyUtil.WorkNotificationId)
    }
}
