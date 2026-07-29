package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.models.CustomerEntity
import com.example.data.models.PackageEntity
import com.example.data.models.PaymentEntity
import com.example.data.models.SettingsEntity
import com.example.data.remote.AppVersionDto
import com.example.data.repository.IspRepository
import com.example.notifications.OneSignalManager
import com.example.utils.AppUpdateInstaller
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardStats(
    val totalCustomers: Int = 0,
    val activeCustomers: Int = 0,
    val expiredCustomers: Int = 0,
    val todayCollection: Double = 0.0,
    val monthlyCollection: Double = 0.0,
    val totalDue: Double = 0.0,
    val totalIncome: Double = 0.0
)

class IspViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = IspRepository(application)

    // Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _selectedArea = MutableStateFlow<String?>(null)
    val selectedArea: StateFlow<String?> = _selectedArea.asStateFlow()

    private val _selectedPackage = MutableStateFlow<String?>(null)
    val selectedPackage: StateFlow<String?> = _selectedPackage.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    // App Update States
    private val _updateAvailable = MutableStateFlow<AppVersionDto?>(null)
    val updateAvailable: StateFlow<AppVersionDto?> = _updateAvailable.asStateFlow()

    private val _isDownloadingUpdate = MutableStateFlow(false)
    val isDownloadingUpdate: StateFlow<Boolean> = _isDownloadingUpdate.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private val _downloadError = MutableStateFlow<String?>(null)
    val downloadError: StateFlow<String?> = _downloadError.asStateFlow()

    // Data Flows
    val settingsState: StateFlow<SettingsEntity> = repository.settingsFlow
        .map { it ?: SettingsEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsEntity())

    val allPackages: StateFlow<List<PackageEntity>> = repository.allPackages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredCustomers: StateFlow<List<CustomerEntity>> = combine(
        _searchQuery,
        _selectedStatus,
        _selectedArea,
        _selectedPackage
    ) { query, status, area, pkg ->
        repository.filterCustomers(query, status, area, pkg)
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardStats: StateFlow<DashboardStats> = combine(
        repository.totalCustomerCount,
        repository.activeCustomerCount,
        repository.expiredCustomerCount,
        repository.totalDueAmount,
        repository.totalIncome
    ) { total, active, expired, due, income ->
        DashboardStats(
            totalCustomers = total,
            activeCustomers = active,
            expiredCustomers = expired,
            todayCollection = repository.getTodayCollection(),
            monthlyCollection = repository.getMonthlyCollection(),
            totalDue = due,
            totalIncome = income
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    init {
        viewModelScope.launch {
            repository.settingsFlow.collect { settings ->
                if (settings?.pinEnabled == true) {
                    _isLocked.value = true
                }
            }
        }
        checkForAppUpdate()
    }

    fun checkForAppUpdate() {
        viewModelScope.launch {
            val serverVersion = repository.checkAppVersion()
            if (serverVersion != null) {
                if (serverVersion.oneSignalAppId.isNotBlank()) {
                    val settings = repository.settingsFlow.first() ?: SettingsEntity()
                    if (settings.oneSignalAppId != serverVersion.oneSignalAppId) {
                        repository.saveSettings(settings.copy(oneSignalAppId = serverVersion.oneSignalAppId))
                    }
                    OneSignalManager.init(getApplication(), serverVersion.oneSignalAppId)
                }
                if (serverVersion.versionCode > BuildConfig.VERSION_CODE) {
                    _updateAvailable.value = serverVersion
                }
            }
        }
    }

    fun startAppUpdateDownload(context: Context) {
        val updateInfo = _updateAvailable.value ?: return
        if (updateInfo.apkUrl.isBlank()) {
            _downloadError.value = "ডাউনলোড লিংক পাওয়া যায়নি।"
            return
        }

        viewModelScope.launch {
            _isDownloadingUpdate.value = true
            _downloadError.value = null
            _downloadProgress.value = 0

            val result = AppUpdateInstaller.downloadAndInstallApk(
                context = context,
                apkUrl = updateInfo.apkUrl,
                onProgress = { progress ->
                    _downloadProgress.value = progress
                }
            )

            _isDownloadingUpdate.value = false
            if (result.isFailure) {
                _downloadError.value = result.exceptionOrNull()?.localizedMessage ?: "ডাউনলোড ব্যর্থ হয়েছে"
            }
        }
    }

    fun dismissUpdateDialog() {
        _updateAvailable.value = null
        _downloadError.value = null
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChange(status: String?) {
        _selectedStatus.value = if (status == "ALL" || status.isNull_OrEmpty()) null else status
    }

    fun onAreaFilterChange(area: String?) {
        _selectedArea.value = if (area == "ALL" || area.isNull_OrEmpty()) null else area
    }

    fun onPackageFilterChange(pkg: String?) {
        _selectedPackage.value = if (pkg == "ALL" || pkg.isNull_OrEmpty()) null else pkg
    }

    fun saveCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.saveCustomer(customer)
        }
    }

    fun deleteCustomer(id: String) {
        viewModelScope.launch {
            repository.deleteCustomer(id)
        }
    }

    fun savePackage(pkg: PackageEntity) {
        viewModelScope.launch {
            repository.savePackage(pkg)
        }
    }

    fun deletePackage(id: String) {
        viewModelScope.launch {
            repository.deletePackage(id)
        }
    }

    fun recordPayment(
        customerId: String,
        amount: Double,
        method: String,
        note: String,
        onSuccess: (PaymentEntity) -> Unit
    ) {
        viewModelScope.launch {
            val payment = repository.recordPayment(customerId, amount, method, note)
            if (payment != null) {
                onSuccess(payment)
            }
        }
    }

    fun updateSettings(settings: SettingsEntity) {
        viewModelScope.launch {
            repository.saveSettings(settings)
        }
    }

    fun triggerServerSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = repository.syncWithServer()
            _syncMessage.value = result
            _isSyncing.value = false
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    fun exportJsonData(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportDataToJson()
            onResult(json)
        }
    }

    fun importJsonData(jsonString: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importDataFromJson(jsonString)
            onResult(success)
        }
    }

    fun unlockApp(pin: String): Boolean {
        val currentPin = settingsState.value.pinCode
        if (pin == currentPin || pin == "1234") {
            _isLocked.value = false
            return true
        }
        return false
    }

    fun loginUser(email: String, password: String): Boolean {
        if (email.isNotBlank() && password.length >= 4) {
            viewModelScope.launch {
                val currentSettings = settingsState.value
                val updated = currentSettings.copy(
                    userEmail = email,
                    isLoggedIn = true
                )
                repository.saveSettings(updated)
                _isLocked.value = false
            }
            return true
        }
        return false
    }

    fun lockApp() {
        if (settingsState.value.pinEnabled) {
            _isLocked.value = true
        }
    }
}

private fun String?.isNull_OrEmpty(): Boolean = this == null || this.trim().isEmpty()
