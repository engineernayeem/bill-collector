package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val customerName: String,
    val amount: Double,
    val previousDue: Double = 0.0,
    val currentBill: Double = 0.0,
    val remainingDue: Double = 0.0,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String = "Cash", // Cash, Bkash, Nagad, Rocket, Bank, Other
    val paymentNote: String = "",
    val receiptNumber: String = "REC-${System.currentTimeMillis() % 100000}",
    val userEmail: String = "",
    val syncStatus: String = "SYNCED",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
