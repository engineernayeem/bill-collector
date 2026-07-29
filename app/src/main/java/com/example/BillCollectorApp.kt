package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.notifications.BillReminderManager
import com.example.notifications.OneSignalManager
import com.example.sync.AutoSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BillCollectorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Notification Channels
        BillReminderManager.createNotificationChannel(this)

        // Schedule Background WorkManager Tasks
        BillReminderManager.scheduleDailyReminderWork(this)
        AutoSyncWorker.scheduleAutoSync(this)

        // Initialize OneSignal Push & In-App Notification Engine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = AppDatabase.getDatabase(this@BillCollectorApp).settingsDao().getSettings()
                val appId = settings?.oneSignalAppId ?: ""
                OneSignalManager.init(this@BillCollectorApp, appId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

