package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.MainActivity
import com.example.data.local.AppDatabase
import java.util.concurrent.TimeUnit

class BillReminderManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "bill_collector_reminders"
        const val CHANNEL_NAME = "Bill Due Reminders"
        const val WORK_TAG_REMINDER = "bill_reminder_worker_task"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Automated ISP bill due reminders for internet subscribers"
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun scheduleDailyReminderWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val dailyWork = PeriodicWorkRequestBuilder<BillReminderWorker>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(WORK_TAG_REMINDER)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG_REMINDER,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWork
            )
        }
    }

    fun sendNotification(title: String, message: String, notificationId: Int = System.currentTimeMillis().toInt()) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}

class BillReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(appContext)
            val customerDao = database.customerDao()
            val settingsDao = database.settingsDao()

            val settings = settingsDao.getSettings()
            val reminderThresholdDays = settings?.reminderDays ?: 3

            val now = System.currentTimeMillis()
            val dayMillis = 24L * 60 * 60 * 1000

            val allCustomers = database.customerDao().getAllCustomersList()
            val reminderManager = BillReminderManager(appContext)
            var notificationCount = 0

            allCustomers.forEach { customer ->
                val daysUntilDue = ((customer.expireDate - now) / dayMillis).toInt()

                if (customer.dueAmount > 0 && daysUntilDue in 0..reminderThresholdDays) {
                    val message = "গ্রাহক ${customer.name} (${customer.area}): ${customer.dueAmount} টাকা বিল বাকি আছে।" +
                            if (daysUntilDue == 0) " (আজ পরিশোধের শেষ দিন!)" else " ($daysUntilDue দিন বাকি)"
                    reminderManager.sendNotification(
                        title = "⚠️ কাস্টমার বিল অ্যালার্ট: ${customer.name}",
                        message = message,
                        notificationId = customer.id.hashCode()
                    )
                    notificationCount++
                } else if (daysUntilDue < 0 && customer.dueAmount > 0) {
                    reminderManager.sendNotification(
                        title = "🚨 লাইন মেয়াদউত্তীর্ণ অ্যালার্ট: ${customer.name}",
                        message = "গ্রাহক ${customer.name} এর লাইন মেয়াদ শেষ হয়েছে! বকেয়া: ${customer.dueAmount} টাকা।",
                        notificationId = customer.id.hashCode()
                    )
                    notificationCount++
                }
            }

            if (notificationCount == 0) {
                reminderManager.sendNotification(
                    title = "ISP বিল রিমাইন্ডার সমাপন",
                    message = "আজকের দৈনিক স্ক্যান সম্পন্ন হয়েছে। সকল বিল আপডেট রয়েছে।",
                    notificationId = 1001
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
