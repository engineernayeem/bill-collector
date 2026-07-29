package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val userEmail: String = "nayeemmallik801@gmail.com",
    val isLoggedIn: Boolean = false,
    val serverUrl: String = "https://isp-server.cpanel.com/api/",
    val apiKey: String = "",
    val reminderDays: Int = 3, // 7, 3, 1, 0
    val pinEnabled: Boolean = false,
    val pinCode: String = "1234",
    val autoSync: Boolean = true,
    val darkMode: Boolean = false,
    val oneSignalAppId: String = "",
    val lastSyncTime: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis()
)
