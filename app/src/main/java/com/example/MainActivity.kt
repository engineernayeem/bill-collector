package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.UpdateDialog
import com.example.ui.screens.*
import com.example.ui.theme.BillCollectorTheme
import com.example.ui.viewmodel.IspViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "ড্যাশবোর্ড", Icons.Default.Dashboard)
    object Customers : Screen("customers", "গ্রাহকসমূহ", Icons.Default.People)
    object PaymentHistory : Screen("payment_history", "পেমেন্টসমূহ", Icons.Default.Receipt)
    object Settings : Screen("settings", "সেটিংস", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: IspViewModel = viewModel()
            val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()

            BillCollectorTheme(darkTheme = settingsState.darkMode) {
                MainAppNavHost(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppNavHost(viewModel: IspViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()

    val updateAvailable by viewModel.updateAvailable.collectAsStateWithLifecycle()
    val isDownloadingUpdate by viewModel.isDownloadingUpdate.collectAsStateWithLifecycle()
    val downloadProgress by viewModel.downloadProgress.collectAsStateWithLifecycle()
    val downloadError by viewModel.downloadError.collectAsStateWithLifecycle()

    updateAvailable?.let { updateInfo ->
        UpdateDialog(
            updateInfo = updateInfo,
            isDownloading = isDownloadingUpdate,
            downloadProgress = downloadProgress,
            errorMessage = downloadError,
            onUpdateClick = { viewModel.startAppUpdateDownload(context) },
            onDismissClick = { viewModel.dismissUpdateDialog() }
        )
    }

    if (isLocked) {
        LoginScreen(
            onEmailLogin = { email, password ->
                viewModel.loginUser(email, password)
            },
            onSuccessLogin = { }
        )
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(
        Screen.Dashboard,
        Screen.Customers,
        Screen.PaymentHistory,
        Screen.Settings
    )

    val showBottomBar = currentRoute in bottomBarScreens.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    bottomBarScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(
                    onNavigateNext = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
                val recentCustomers by viewModel.filteredCustomers.collectAsStateWithLifecycle()
                val bannerAds by viewModel.bannerAds.collectAsStateWithLifecycle()
                val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

                DashboardScreen(
                    userEmail = settingsState.userEmail,
                    stats = stats,
                    recentCustomers = recentCustomers,
                    bannerAds = bannerAds,
                    isSyncing = isSyncing,
                    onNavigateToAddCustomer = { navController.navigate("add_edit_customer") },
                    onNavigateToCustomers = { navController.navigate(Screen.Customers.route) },
                    onNavigateToCustomerDetail = { id -> navController.navigate("customer_detail/$id") },
                    onNavigateToPaymentScreen = { id -> navController.navigate("payment/$id") },
                    onNavigateToPackages = { navController.navigate("packages") },
                    onNavigateToReports = { navController.navigate("reports") },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onTriggerSync = { viewModel.triggerServerSync() }
                )
            }

            composable(Screen.Customers.route) {
                val customers by viewModel.filteredCustomers.collectAsStateWithLifecycle()
                val bannerAds by viewModel.bannerAds.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
                val selectedArea by viewModel.selectedArea.collectAsStateWithLifecycle()
                val selectedPackage by viewModel.selectedPackage.collectAsStateWithLifecycle()

                CustomerListScreen(
                    customers = customers,
                    bannerAds = bannerAds,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    selectedStatus = selectedStatus,
                    onStatusFilterChange = { viewModel.onStatusFilterChange(it) },
                    selectedArea = selectedArea,
                    onAreaFilterChange = { viewModel.onAreaFilterChange(it) },
                    selectedPackage = selectedPackage,
                    onPackageFilterChange = { viewModel.onPackageFilterChange(it) },
                    onNavigateToAddCustomer = { navController.navigate("add_edit_customer") },
                    onNavigateToCustomerDetail = { id -> navController.navigate("customer_detail/$id") },
                    onNavigateToPaymentScreen = { id -> navController.navigate("payment/$id") }
                )
            }

            composable(
                route = "customer_detail/{customerId}",
                arguments = listOf(navArgument("customerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
                val allCustomers by viewModel.filteredCustomers.collectAsStateWithLifecycle()
                val customer = allCustomers.find { it.id == customerId }
                val allPayments by viewModel.allPayments.collectAsStateWithLifecycle()
                val customerPayments = allPayments.filter { it.customerId == customerId }

                CustomerDetailScreen(
                    customer = customer,
                    payments = customerPayments,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { id -> navController.navigate("add_edit_customer?customerId=$id") },
                    onDeleteClick = { id ->
                        viewModel.deleteCustomer(id)
                        navController.popBackStack()
                    },
                    onRecordPaymentClick = { id -> navController.navigate("payment/$id") }
                )
            }

            composable(
                route = "add_edit_customer?customerId={customerId}",
                arguments = listOf(navArgument("customerId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getString("customerId")
                val allCustomers by viewModel.filteredCustomers.collectAsStateWithLifecycle()
                val existingCustomer = allCustomers.find { it.id == customerId }
                val packages by viewModel.allPackages.collectAsStateWithLifecycle()

                AddEditCustomerScreen(
                    existingCustomer = existingCustomer,
                    packages = packages,
                    onBackClick = { navController.popBackStack() },
                    onSaveCustomer = { customer ->
                        viewModel.saveCustomer(customer)
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "payment/{customerId}",
                arguments = listOf(navArgument("customerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
                val allCustomers by viewModel.filteredCustomers.collectAsStateWithLifecycle()
                val customer = allCustomers.find { it.id == customerId }

                PaymentScreen(
                    customer = customer,
                    onBackClick = { navController.popBackStack() },
                    onConfirmPayment = { cId, amount, method, note, onSuccess ->
                        viewModel.recordPayment(cId, amount, method, note, onSuccess)
                    }
                )
            }

            composable(Screen.PaymentHistory.route) {
                val payments by viewModel.allPayments.collectAsStateWithLifecycle()

                PaymentHistoryScreen(
                    payments = payments,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("packages") {
                val packages by viewModel.allPackages.collectAsStateWithLifecycle()

                PackageManagementScreen(
                    packages = packages,
                    onBackClick = { navController.popBackStack() },
                    onSavePackage = { pkg -> viewModel.savePackage(pkg) },
                    onDeletePackage = { id -> viewModel.deletePackage(id) }
                )
            }

            composable("reports") {
                val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()

                ReportsScreen(
                    stats = stats,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                val settings by viewModel.settingsState.collectAsStateWithLifecycle()
                val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
                val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()

                SettingsScreen(
                    settings = settings,
                    isSyncing = isSyncing,
                    syncMessage = syncMessage,
                    onBackClick = { navController.popBackStack() },
                    onSaveSettings = { newSettings -> viewModel.updateSettings(newSettings) },
                    onTriggerSync = { viewModel.triggerServerSync() },
                    onExportJson = { onResult -> viewModel.exportJsonData(onResult) },
                    onImportJson = { json, onResult -> viewModel.importJsonData(json, onResult) },
                    onClearSyncMessage = { viewModel.clearSyncMessage() },
                    onCheckUpdate = { viewModel.checkForAppUpdate() },
                    onNavigateToLogin = { navController.navigate("login") }
                )
            }

            composable("login") {
                val settings by viewModel.settingsState.collectAsStateWithLifecycle()
                LoginScreen(
                    currentSavedEmail = settings.userEmail,
                    onEmailLogin = { email, _ ->
                        viewModel.updateSettings(settings.copy(userEmail = email))
                        true
                    },
                    onSuccessLogin = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
