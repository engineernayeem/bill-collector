package com.example.data.local

import androidx.room.*
import com.example.data.models.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY updatedAt DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers ORDER BY updatedAt DESC")
    suspend fun getAllCustomersList(): List<CustomerEntity>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    fun getCustomerByIdFlow(id: String): Flow<CustomerEntity?>

    @Query("""
        SELECT * FROM customers 
        WHERE (:query IS NULL OR :query = '' OR name LIKE '%' || :query || '%' OR mobileNumber LIKE '%' || :query || '%' OR customerId LIKE '%' || :query || '%' OR area LIKE '%' || :query || '%')
        AND (:status IS NULL OR :status = '' OR connectionStatus = :status)
        AND (:area IS NULL OR :area = '' OR area = :area)
        AND (:pkg IS NULL OR :pkg = '' OR packageName = :pkg)
        ORDER BY updatedAt DESC
    """)
    fun filterCustomers(
        query: String?,
        status: String?,
        area: String?,
        pkg: String?
    ): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncCustomers(): List<CustomerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerEntity>)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Query("DELETE FROM customers WHERE id = :id")
    suspend fun deleteCustomerById(id: String)

    @Query("SELECT COUNT(*) FROM customers")
    fun getTotalCustomerCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM customers WHERE connectionStatus = 'ACTIVE'")
    fun getActiveCustomerCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM customers WHERE connectionStatus = 'EXPIRED'")
    fun getExpiredCustomerCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(dueAmount), 0.0) FROM customers")
    fun getTotalDueAmount(): Flow<Double>
}
