package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "packages")
data class PackageEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val speedMbps: Int,
    val monthlyPrice: Double,
    val description: String = "",
    val userEmail: String = "",
    val syncStatus: String = "SYNCED",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
