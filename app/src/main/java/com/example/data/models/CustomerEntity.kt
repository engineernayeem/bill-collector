package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val name: String,
    val mobileNumber: String,
    val altMobileNumber: String = "",
    val address: String = "",
    val area: String = "",
    val connectionStatus: String = "ACTIVE", // ACTIVE, EXPIRED, SUSPENDED
    val packageName: String = "20 Mbps",
    val packageSpeedMbps: Int = 20,
    val monthlyBill: Double = 1000.0,
    val installationCharge: Double = 0.0,
    val connectionDate: Long = System.currentTimeMillis(),
    val billingDate: Int = 5, // Day of month (1-31)
    val expireDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val lastPaymentDate: Long = System.currentTimeMillis(),
    val nextPaymentDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val dueAmount: Double = 0.0,
    val advanceAmount: Double = 0.0,
    val discount: Double = 0.0,
    val remarks: String = "",
    val photoUri: String? = null,
    val userEmail: String = "",
    val syncStatus: String = "SYNCED", // SYNCED, PENDING_UPDATE, PENDING_DELETE
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
