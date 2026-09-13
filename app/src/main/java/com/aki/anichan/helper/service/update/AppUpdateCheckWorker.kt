package com.aki.anichan.helper.service.update

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class AppUpdateCheckWorker(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        return try {
            val checker: AppUpdateChecker = org.koin.java.KoinJavaComponent.get(AppUpdateChecker::class.java)
            val info = checker.checkBlocking()
            if (info != null && info.isNewer) {
                AppUpdateNotifier.notifyIfNeeded(applicationContext, checker, info)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_WORK = "appUpdateCheckWork"

        fun scheduleDaily(context: Context) {
            val request = PeriodicWorkRequestBuilder<AppUpdateCheckWorker>(1, TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_WORK, ExistingPeriodicWorkPolicy.KEEP, request)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK)
        }
    }
}
