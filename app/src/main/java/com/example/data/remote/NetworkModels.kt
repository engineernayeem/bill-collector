package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CustomerNetworkDto(
    @Json(name = "id") val id: String,
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "name") val name: String,
    @Json(name = "mobile_number") val mobileNumber: String,
    @Json(name = "alt_mobile_number") val altMobileNumber: String = "",
    @Json(name = "address") val address: String = "",
    @Json(name = "area") val area: String = "",
    @Json(name = "connection_status") val connectionStatus: String = "ACTIVE",
    @Json(name = "package_name") val packageName: String = "",
    @Json(name = "package_speed_mbps") val packageSpeedMbps: Int = 0,
    @Json(name = "monthly_bill") val monthlyBill: Double = 0.0,
    @Json(name = "installation_charge") val installationCharge: Double = 0.0,
    @Json(name = "connection_date") val connectionDate: Long = 0L,
    @Json(name = "billing_date") val billingDate: Int = 1,
    @Json(name = "expire_date") val expireDate: Long = 0L,
    @Json(name = "last_payment_date") val lastPaymentDate: Long = 0L,
    @Json(name = "next_payment_date") val nextPaymentDate: Long = 0L,
    @Json(name = "due_amount") val dueAmount: Double = 0.0,
    @Json(name = "advance_amount") val advanceAmount: Double = 0.0,
    @Json(name = "discount") val discount: Double = 0.0,
    @Json(name = "remarks") val remarks: String = "",
    @Json(name = "photo_uri") val photoUri: String? = null,
    @Json(name = "created_at") val createdAt: Long = 0L,
    @Json(name = "updated_at") val updatedAt: Long = 0L
)

@JsonClass(generateAdapter = true)
data class PackageNetworkDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "speed_mbps") val speedMbps: Int,
    @Json(name = "monthly_price") val monthlyPrice: Double,
    @Json(name = "description") val description: String = "",
    @Json(name = "created_at") val createdAt: Long = 0L,
    @Json(name = "updated_at") val updatedAt: Long = 0L
)

@JsonClass(generateAdapter = true)
data class PaymentNetworkDto(
    @Json(name = "id") val id: String,
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "customer_name") val customerName: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "previous_due") val previousDue: Double = 0.0,
    @Json(name = "current_bill") val currentBill: Double = 0.0,
    @Json(name = "remaining_due") val remainingDue: Double = 0.0,
    @Json(name = "payment_date") val paymentDate: Long = 0L,
    @Json(name = "payment_method") val paymentMethod: String = "Cash",
    @Json(name = "payment_note") val paymentNote: String = "",
    @Json(name = "receipt_number") val receiptNumber: String = "",
    @Json(name = "created_at") val createdAt: Long = 0L,
    @Json(name = "updated_at") val updatedAt: Long = 0L
)

@JsonClass(generateAdapter = true)
data class FullBackupDataDto(
    @Json(name = "app_name") val appName: String = "Bill Collector",
    @Json(name = "export_timestamp") val exportTimestamp: Long = System.currentTimeMillis(),
    @Json(name = "customers") val customers: List<CustomerNetworkDto> = emptyList(),
    @Json(name = "packages") val packages: List<PackageNetworkDto> = emptyList(),
    @Json(name = "payments") val payments: List<PaymentNetworkDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SyncResponseDto(
    @Json(name = "status") val status: String = "success",
    @Json(name = "message") val message: String = "",
    @Json(name = "synced_timestamp") val syncedTimestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class BannerAdDto(
    @Json(name = "id") val id: String = "1",
    @Json(name = "title") val title: String = "",
    @Json(name = "imageUrl") val imageUrl: String = "",
    @Json(name = "targetUrl") val targetUrl: String = "",
    @Json(name = "active") val active: Boolean = true,
    @Json(name = "position") val position: String = "dashboard"
)

@JsonClass(generateAdapter = true)
data class AppVersionDto(
    @Json(name = "versionCode") val versionCode: Int = 1,
    @Json(name = "versionName") val versionName: String = "1.0.0",
    @Json(name = "apkUrl") val apkUrl: String = "",
    @Json(name = "releaseNotes") val releaseNotes: String = "",
    @Json(name = "forceUpdate") val forceUpdate: Boolean = false,
    @Json(name = "oneSignalAppId") val oneSignalAppId: String = "",
    @Json(name = "bannerAds") val bannerAds: List<BannerAdDto> = emptyList()
)
