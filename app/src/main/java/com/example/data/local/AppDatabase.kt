package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.CustomerEntity
import com.example.data.models.PackageEntity
import com.example.data.models.PaymentEntity
import com.example.data.models.SettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [CustomerEntity::class, PackageEntity::class, PaymentEntity::class, SettingsEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun packageDao(): PackageDao
    abstract fun paymentDao(): PaymentDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bill_collector_db"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                // Initial Settings
                database.settingsDao().insertOrUpdate(SettingsEntity())

                // Initial Internet Packages
                val defaultPackages = listOf(
                    PackageEntity(id = "pkg-10", name = "10 Mbps Basic", speedMbps = 10, monthlyPrice = 500.0, description = "Home starter connection"),
                    PackageEntity(id = "pkg-20", name = "20 Mbps Standard", speedMbps = 20, monthlyPrice = 800.0, description = "Optimal for HD streaming"),
                    PackageEntity(id = "pkg-30", name = "30 Mbps Ultra", speedMbps = 30, monthlyPrice = 1100.0, description = "Fast gaming & multi-device"),
                    PackageEntity(id = "pkg-50", name = "50 Mbps Pro", speedMbps = 50, monthlyPrice = 1600.0, description = "Business & high performance"),
                    PackageEntity(id = "pkg-100", name = "100 Mbps Fiber", speedMbps = 100, monthlyPrice = 2800.0, description = "Ultra fiber speed")
                )
                database.packageDao().insertAll(defaultPackages)

                // Initial Customers
                val now = System.currentTimeMillis()
                val dayMillis = 24L * 60 * 60 * 1000
                val defaultCustomers = listOf(
                    CustomerEntity(
                        id = "cust-1",
                        customerId = "ISP-1001",
                        name = "Rahim Ahmed",
                        mobileNumber = "+8801712345678",
                        altMobileNumber = "+8801812345678",
                        address = "House 12, Road 5, Block C",
                        area = "Gulshan",
                        connectionStatus = "ACTIVE",
                        packageName = "20 Mbps Standard",
                        packageSpeedMbps = 20,
                        monthlyBill = 800.0,
                        installationCharge = 1000.0,
                        billingDate = 5,
                        expireDate = now + (15 * dayMillis),
                        nextPaymentDate = now + (5 * dayMillis),
                        dueAmount = 0.0,
                        remarks = "VIP Customer - Fiber connection"
                    ),
                    CustomerEntity(
                        id = "cust-2",
                        customerId = "ISP-1002",
                        name = "Fatema Begum",
                        mobileNumber = "+8801987654321",
                        address = "Plot 44, Main Street",
                        area = "Dhanmondi",
                        connectionStatus = "ACTIVE",
                        packageName = "30 Mbps Ultra",
                        packageSpeedMbps = 30,
                        monthlyBill = 1100.0,
                        billingDate = 10,
                        expireDate = now + (20 * dayMillis),
                        nextPaymentDate = now + (1 * dayMillis), // Due tomorrow!
                        dueAmount = 1100.0,
                        remarks = "Prefers Bkash payment"
                    ),
                    CustomerEntity(
                        id = "cust-3",
                        customerId = "ISP-1003",
                        name = "Tanvir Hasan",
                        mobileNumber = "+8801555666777",
                        address = "Apartment 4B, Blue Tower",
                        area = "Uttara",
                        connectionStatus = "EXPIRED",
                        packageName = "10 Mbps Basic",
                        packageSpeedMbps = 10,
                        monthlyBill = 500.0,
                        billingDate = 1,
                        expireDate = now - (2 * dayMillis), // Expired
                        nextPaymentDate = now - (2 * dayMillis),
                        dueAmount = 500.0,
                        remarks = "Bill overdue by 2 days"
                    ),
                    CustomerEntity(
                        id = "cust-4",
                        customerId = "ISP-1004",
                        name = "Karim Uddin",
                        mobileNumber = "+8801611223344",
                        address = "Sector 7, Road 18",
                        area = "Uttara",
                        connectionStatus = "ACTIVE",
                        packageName = "50 Mbps Pro",
                        packageSpeedMbps = 50,
                        monthlyBill = 1600.0,
                        billingDate = 15,
                        expireDate = now + (25 * dayMillis),
                        nextPaymentDate = now + (12 * dayMillis),
                        dueAmount = 0.0,
                        advanceAmount = 400.0,
                        remarks = "Office line"
                    )
                )
                database.customerDao().insertAll(defaultCustomers)

                // Initial Payments
                val defaultPayments = listOf(
                    PaymentEntity(
                        id = UUID.randomUUID().toString(),
                        customerId = "cust-1",
                        customerName = "Rahim Ahmed",
                        amount = 800.0,
                        previousDue = 800.0,
                        currentBill = 800.0,
                        remainingDue = 0.0,
                        paymentDate = now - (15 * dayMillis),
                        paymentMethod = "Bkash",
                        paymentNote = "July Monthly Bill Paid",
                        receiptNumber = "REC-88201"
                    ),
                    PaymentEntity(
                        id = UUID.randomUUID().toString(),
                        customerId = "cust-4",
                        customerName = "Karim Uddin",
                        amount = 2000.0,
                        previousDue = 1600.0,
                        currentBill = 1600.0,
                        remainingDue = 0.0,
                        paymentDate = now - (2 * dayMillis),
                        paymentMethod = "Cash",
                        paymentNote = "Advance payment included",
                        receiptNumber = "REC-88202"
                    )
                )
                database.paymentDao().insertAll(defaultPayments)
            }
        }
    }
}
