package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.models.CustomerEntity
import com.example.data.models.PackageEntity
import com.example.data.models.PaymentEntity
import com.example.data.models.SettingsEntity
import com.example.data.remote.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

class IspRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val customerDao = database.customerDao()
    private val packageDao = database.packageDao()
    private val paymentDao = database.paymentDao()
    private val settingsDao = database.settingsDao()

    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    val allPackages: Flow<List<PackageEntity>> = packageDao.getAllPackages()
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
    val settingsFlow: Flow<SettingsEntity?> = settingsDao.getSettingsFlow()

    val totalCustomerCount: Flow<Int> = customerDao.getTotalCustomerCount()
    val activeCustomerCount: Flow<Int> = customerDao.getActiveCustomerCount()
    val expiredCustomerCount: Flow<Int> = customerDao.getExpiredCustomerCount()
    val totalDueAmount: Flow<Double> = customerDao.getTotalDueAmount()
    val totalIncome: Flow<Double> = paymentDao.getTotalIncome()

    fun filterCustomers(query: String?, status: String?, area: String?, pkg: String?): Flow<List<CustomerEntity>> {
        return customerDao.filterCustomers(query, status, area, pkg)
    }

    fun getPaymentsForCustomer(customerId: String): Flow<List<PaymentEntity>> {
        return paymentDao.getPaymentsForCustomer(customerId)
    }

    fun getCustomerByIdFlow(customerId: String): Flow<CustomerEntity?> {
        return customerDao.getCustomerByIdFlow(customerId)
    }

    suspend fun getCustomerById(id: String): CustomerEntity? {
        return customerDao.getCustomerById(id)
    }

    suspend fun saveCustomer(customer: CustomerEntity) {
        val updatedCustomer = customer.copy(
            updatedAt = System.currentTimeMillis(),
            syncStatus = "PENDING_UPDATE"
        )
        customerDao.insertCustomer(updatedCustomer)
    }

    suspend fun deleteCustomer(id: String) {
        customerDao.deleteCustomerById(id)
    }

    suspend fun savePackage(pkg: PackageEntity) {
        val updatedPackage = pkg.copy(
            updatedAt = System.currentTimeMillis(),
            syncStatus = "PENDING_UPDATE"
        )
        packageDao.insertPackage(updatedPackage)
    }

    suspend fun deletePackage(id: String) {
        packageDao.deletePackageById(id)
    }

    suspend fun recordPayment(
        customerId: String,
        paidAmount: Double,
        paymentMethod: String,
        note: String
    ): PaymentEntity? {
        val customer = customerDao.getCustomerById(customerId) ?: return null
        val previousDue = customer.dueAmount
        val currentBill = customer.monthlyBill
        val totalOwed = previousDue + currentBill
        val remainingDue = (totalOwed - paidAmount).coerceAtLeast(0.0)
        val advance = (paidAmount - totalOwed).coerceAtLeast(0.0)

        val now = System.currentTimeMillis()
        val payment = PaymentEntity(
            id = UUID.randomUUID().toString(),
            customerId = customer.id,
            customerName = customer.name,
            amount = paidAmount,
            previousDue = previousDue,
            currentBill = currentBill,
            remainingDue = remainingDue,
            paymentDate = now,
            paymentMethod = paymentMethod,
            paymentNote = note,
            receiptNumber = "REC-${(now / 1000) % 1000000}",
            syncStatus = "PENDING_UPDATE",
            createdAt = now,
            updatedAt = now
        )

        paymentDao.insertPayment(payment)

        // Update customer due and payment dates
        val monthMillis = 30L * 24 * 60 * 60 * 1000
        val updatedCustomer = customer.copy(
            dueAmount = remainingDue,
            advanceAmount = advance,
            lastPaymentDate = now,
            nextPaymentDate = now + monthMillis,
            expireDate = now + monthMillis,
            connectionStatus = "ACTIVE",
            updatedAt = now,
            syncStatus = "PENDING_UPDATE"
        )
        customerDao.updateCustomer(updatedCustomer)

        return payment
    }

    suspend fun saveSettings(settings: SettingsEntity) {
        settingsDao.insertOrUpdate(settings.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun getTodayCollection(): Double {
        val startOfDay = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000))
        return paymentDao.getTodayCollection(startOfDay).first()
    }

    suspend fun getMonthlyCollection(): Double {
        val startOfMonth = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        return paymentDao.getMonthlyCollection(startOfMonth).first()
    }

    // JSON Export and Import
    suspend fun exportDataToJson(): String = withContext(Dispatchers.IO) {
        val customers = customerDao.getAllCustomers().first()
        val packages = packageDao.getAllPackages().first()
        val payments = paymentDao.getAllPayments().first()

        val dto = FullBackupDataDto(
            appName = "Bill Collector",
            exportTimestamp = System.currentTimeMillis(),
            customers = customers.map { it.toNetworkDto() },
            packages = packages.map { it.toNetworkDto() },
            payments = payments.map { it.toNetworkDto() }
        )

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(FullBackupDataDto::class.java).indent("  ")
        adapter.toJson(dto)
    }

    suspend fun importDataFromJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(FullBackupDataDto::class.java)
            val backup = adapter.fromJson(jsonString) ?: return@withContext false

            val newCustomers = backup.customers.map { it.toEntity() }
            val newPackages = backup.packages.map { it.toEntity() }
            val newPayments = backup.payments.map { it.toEntity() }

            if (newCustomers.isNotEmpty()) customerDao.insertAll(newCustomers)
            if (newPackages.isNotEmpty()) packageDao.insertAll(newPackages)
            if (newPayments.isNotEmpty()) paymentDao.insertAll(newPayments)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Sync Engine (Conflict resolution: Latest Updated Wins)
    suspend fun syncWithServer(): String = withContext(Dispatchers.IO) {
        val settings = settingsDao.getSettings() ?: SettingsEntity()
        var baseUrl = settings.serverUrl.trim()
        if (baseUrl.isBlank()) return@withContext "Server URL not configured."
        if (!baseUrl.endsWith("/")) baseUrl += "/"

        try {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            // 1. Upload Pending Local Customers
            val pendingCustomers = customerDao.getPendingSyncCustomers()
            if (pendingCustomers.isNotEmpty()) {
                val customerDtos = pendingCustomers.map { it.toNetworkDto() }
                apiService.uploadCustomers(customerDtos)
                customerDao.insertAll(pendingCustomers.map { it.copy(syncStatus = "SYNCED") })
            }

            // 2. Upload Pending Payments
            val pendingPayments = paymentDao.getPendingSyncPayments()
            if (pendingPayments.isNotEmpty()) {
                val paymentDtos = pendingPayments.map { it.toNetworkDto() }
                apiService.uploadPayments(paymentDtos)
                paymentDao.insertAll(pendingPayments.map { it.copy(syncStatus = "SYNCED") })
            }

            // 3. Upload Pending Packages
            val pendingPackages = packageDao.getPendingSyncPackages()
            if (pendingPackages.isNotEmpty()) {
                val pkgDtos = pendingPackages.map { it.toNetworkDto() }
                apiService.uploadPackages(pkgDtos)
                packageDao.insertAll(pendingPackages.map { it.copy(syncStatus = "SYNCED") })
            }

            // Update Last Sync Time
            val now = System.currentTimeMillis()
            settingsDao.insertOrUpdate(settings.copy(lastSyncTime = now, updatedAt = now))

            "Sync completed successfully."
        } catch (e: Exception) {
            e.printStackTrace()
            "Sync notice: Server connection offline or unavailable (${e.localizedMessage}). Local data preserved."
        }
    }

    suspend fun checkAppVersion(): AppVersionDto? = withContext(Dispatchers.IO) {
        val settings = settingsDao.getSettings() ?: SettingsEntity()
        var baseUrl = settings.serverUrl.trim()
        if (baseUrl.isBlank()) return@withContext null
        if (!baseUrl.endsWith("/")) baseUrl += "/"

        try {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            val apiService = retrofit.create(ApiService::class.java)
            val response = apiService.getAppVersion()
            if (response.isSuccessful) {
                return@withContext response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }

    suspend fun fetchBannerAds(): List<BannerAdDto> = withContext(Dispatchers.IO) {
        val settings = settingsDao.getSettings() ?: SettingsEntity()
        var baseUrl = settings.serverUrl.trim()
        if (baseUrl.isBlank()) return@withContext emptyList()
        if (!baseUrl.endsWith("/")) baseUrl += "/"

        try {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            // 1. Try version.json bannerAds first
            val versionResponse = apiService.getAppVersion()
            if (versionResponse.isSuccessful && versionResponse.body()?.bannerAds?.isNotEmpty() == true) {
                return@withContext versionResponse.body()!!.bannerAds
            }

            // 2. Try ads.json
            val adsResponse = apiService.getBannerAds()
            if (adsResponse.isSuccessful && adsResponse.body() != null) {
                return@withContext adsResponse.body()!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }
}

// Extension Mappers
fun CustomerEntity.toNetworkDto() = CustomerNetworkDto(
    id = id,
    customerId = customerId,
    name = name,
    mobileNumber = mobileNumber,
    altMobileNumber = altMobileNumber,
    address = address,
    area = area,
    connectionStatus = connectionStatus,
    packageName = packageName,
    packageSpeedMbps = packageSpeedMbps,
    monthlyBill = monthlyBill,
    installationCharge = installationCharge,
    connectionDate = connectionDate,
    billingDate = billingDate,
    expireDate = expireDate,
    lastPaymentDate = lastPaymentDate,
    nextPaymentDate = nextPaymentDate,
    dueAmount = dueAmount,
    advanceAmount = advanceAmount,
    discount = discount,
    remarks = remarks,
    photoUri = photoUri,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun CustomerNetworkDto.toEntity() = CustomerEntity(
    id = id,
    customerId = customerId,
    name = name,
    mobileNumber = mobileNumber,
    altMobileNumber = altMobileNumber,
    address = address,
    area = area,
    connectionStatus = connectionStatus,
    packageName = packageName,
    packageSpeedMbps = packageSpeedMbps,
    monthlyBill = monthlyBill,
    installationCharge = installationCharge,
    connectionDate = connectionDate,
    billingDate = billingDate,
    expireDate = expireDate,
    lastPaymentDate = lastPaymentDate,
    nextPaymentDate = nextPaymentDate,
    dueAmount = dueAmount,
    advanceAmount = advanceAmount,
    discount = discount,
    remarks = remarks,
    photoUri = photoUri,
    syncStatus = "SYNCED",
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PackageEntity.toNetworkDto() = PackageNetworkDto(
    id = id,
    name = name,
    speedMbps = speedMbps,
    monthlyPrice = monthlyPrice,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PackageNetworkDto.toEntity() = PackageEntity(
    id = id,
    name = name,
    speedMbps = speedMbps,
    monthlyPrice = monthlyPrice,
    description = description,
    syncStatus = "SYNCED",
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PaymentEntity.toNetworkDto() = PaymentNetworkDto(
    id = id,
    customerId = customerId,
    customerName = customerName,
    amount = amount,
    previousDue = previousDue,
    currentBill = currentBill,
    remainingDue = remainingDue,
    paymentDate = paymentDate,
    paymentMethod = paymentMethod,
    paymentNote = paymentNote,
    receiptNumber = receiptNumber,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PaymentNetworkDto.toEntity() = PaymentEntity(
    id = id,
    customerId = customerId,
    customerName = customerName,
    amount = amount,
    previousDue = previousDue,
    currentBill = currentBill,
    remainingDue = remainingDue,
    paymentDate = paymentDate,
    paymentMethod = paymentMethod,
    paymentNote = paymentNote,
    receiptNumber = receiptNumber,
    syncStatus = "SYNCED",
    createdAt = createdAt,
    updatedAt = updatedAt
)
