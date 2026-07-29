package com.example.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("customers.json")
    suspend fun getCustomers(): Response<List<CustomerNetworkDto>>

    @POST("customers.json")
    suspend fun uploadCustomers(@Body customers: List<CustomerNetworkDto>): Response<SyncResponseDto>

    @GET("packages.json")
    suspend fun getPackages(): Response<List<PackageNetworkDto>>

    @POST("packages.json")
    suspend fun uploadPackages(@Body packages: List<PackageNetworkDto>): Response<SyncResponseDto>

    @GET("payments.json")
    suspend fun getPayments(): Response<List<PaymentNetworkDto>>

    @POST("payments.json")
    suspend fun uploadPayments(@Body payments: List<PaymentNetworkDto>): Response<SyncResponseDto>

    @POST("sync_full.json")
    suspend fun syncAllData(@Body fullBackup: FullBackupDataDto): Response<SyncResponseDto>

    @GET("version.json")
    suspend fun getAppVersion(): Response<AppVersionDto>
}
