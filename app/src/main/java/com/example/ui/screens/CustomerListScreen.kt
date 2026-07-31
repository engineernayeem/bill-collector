package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CustomerEntity
import com.example.data.remote.BannerAdDto
import com.example.ui.components.BannerAdCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    customers: List<CustomerEntity>,
    bannerAds: List<BannerAdDto> = emptyList(),
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatus: String?,
    onStatusFilterChange: (String?) -> Unit,
    selectedArea: String?,
    onAreaFilterChange: (String?) -> Unit,
    selectedPackage: String?,
    onPackageFilterChange: (String?) -> Unit,
    onNavigateToAddCustomer: () -> Unit,
    onNavigateToCustomerDetail: (String) -> Unit,
    onNavigateToPaymentScreen: (String) -> Unit
) {
    val statusFilters = listOf("ALL", "ACTIVE", "EXPIRED", "DUE")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("গ্রাহক তালিকা (${customers.size} জন)", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCustomer,
                containerColor = PrimaryBlue,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_customer_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "নতুন গ্রাহক যোগ করুন")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("customer_search_input"),
                placeholder = { Text("নাম, মোবাইল, আইডি বা এলাকা দিয়ে খুঁজুন...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "খুঁজুন") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "ক্লিয়ার")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Status Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusFilters) { filter ->
                    val isSelected = (selectedStatus == null && filter == "ALL") || selectedStatus == filter
                    val labelText = when (filter) {
                        "ALL" -> "সকল"
                        "ACTIVE" -> "সক্রিয়"
                        "EXPIRED" -> "মেয়াদউত্তীর্ণ"
                        "DUE" -> "বকেয়া"
                        else -> filter
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onStatusFilterChange(if (filter == "ALL") null else filter) },
                        label = { Text(labelText) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Customer List
            if (customers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PeopleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "কোনো গ্রাহক পাওয়া যায়নি।",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (bannerAds.isNotEmpty()) {
                        item {
                            BannerAdCard(
                                ads = bannerAds,
                                position = "customer_list",
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }

                    items(customers) { customer ->
                        CustomerCardItemBangla(
                            customer = customer,
                            onClick = { onNavigateToCustomerDetail(customer.id) },
                            onPayClick = { onNavigateToPaymentScreen(customer.id) }
                        )
                    }
                }
            }
        }
    }
}
