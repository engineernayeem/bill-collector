package com.example.data.local

import androidx.room.*
import com.example.data.models.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY paymentDate DESC")
    fun getPaymentsForCustomer(customerId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncPayments(): List<PaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(payments: List<PaymentEntity>)

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM payments WHERE paymentDate >= :startOfDay")
    fun getTodayCollection(startOfDay: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM payments WHERE paymentDate >= :startOfMonth")
    fun getMonthlyCollection(startOfMonth: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM payments")
    fun getTotalIncome(): Flow<Double>
}
