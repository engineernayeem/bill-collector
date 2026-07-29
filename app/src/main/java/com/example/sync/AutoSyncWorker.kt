package com.example.sync

import android.content.Context
import androidx.work.*
import com.example.data.repository.IspRepository
import java.util.concurrent.TimeUnit

class AutoSyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val repository = IspRepository(appContext)
            val resultMessage = repository.syncWithServer()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_TAG_AUTO_SYNC = "auto_sync_cpanel_worker"

        fun scheduleAutoSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWork = PeriodicWorkRequestBuilder<AutoSyncWorker>(2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(WORK_TAG_AUTO_SYNC)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG_AUTO_SYNC,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWork
            )
        }
    }
}
