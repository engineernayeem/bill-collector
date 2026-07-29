package com.example.data.local

import androidx.room.*
import com.example.data.models.PackageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageDao {
    @Query("SELECT * FROM packages ORDER BY speedMbps ASC")
    fun getAllPackages(): Flow<List<PackageEntity>>

    @Query("SELECT * FROM packages WHERE id = :id LIMIT 1")
    suspend fun getPackageById(id: String): PackageEntity?

    @Query("SELECT * FROM packages WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSyncPackages(): List<PackageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: PackageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(packages: List<PackageEntity>)

    @Update
    suspend fun updatePackage(pkg: PackageEntity)

    @Query("DELETE FROM packages WHERE id = :id")
    suspend fun deletePackageById(id: String)
}
