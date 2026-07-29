package com.example.notifications

import android.content.Context
import android.util.Log
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object OneSignalManager {

    private const val TAG = "OneSignalManager"

    fun init(context: Context, appId: String) {
        if (appId.isBlank()) {
            Log.d(TAG, "OneSignal App ID is empty. Push notifications disabled.")
            return
        }

        try {
            // OneSignal v5 initialization
            OneSignal.initWithContext(context, appId)
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    OneSignal.Notifications.requestPermission(true)
                } catch (e: Exception) {
                    Log.e(TAG, "Permission request failed: ${e.message}")
                }
            }
            Log.i(TAG, "OneSignal initialized successfully with App ID: $appId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize OneSignal: ${e.message}")
        }
    }

    fun setCustomerTags(customerId: String, area: String, status: String) {
        try {
            OneSignal.User.addTag("customer_id", customerId)
            OneSignal.User.addTag("area", area)
            OneSignal.User.addTag("status", status)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set OneSignal tags: ${e.message}")
        }
    }
}
