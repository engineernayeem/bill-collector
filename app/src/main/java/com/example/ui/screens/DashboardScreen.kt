package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.CustomerEntity
import com.example.data.remote.BannerAdDto
import com.example.ui.components.BannerAdCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashboardStats
import com.example.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userEmail: String = "nayeemmallik801@gmail.com",
    stats: DashboardStats,
    recentCustomers: List<CustomerEntity>,
    bannerAds: List<BannerAdDto> = emptyList(),
    isSyncing: Boolean,
    onNavigateToAddCustomer: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToCustomerDetail: (String) -> Unit,
    onNavigateToPaymentScreen: (String) -> Unit,
    onNavigateToPackages: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onTriggerSync: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "বিল কালেক্টর",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "লগইনকৃত: $userEmail",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onTriggerSync,
                        modifier = Modifier.testTag("dashboard_sync_button")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = PrimaryBlue
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "সার্ভার সিঙ্ক",
                                tint = PrimaryBlue
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "সেটিংস"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.hero_isp_dashboard),
                            contentDescription = "আইএসপি ব্যানার",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            PrimaryBlue.copy(alpha = 0.88f),
                                            PrimaryBlueDark.copy(alpha = 0.60f)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = "⚡ অফলাইন-ফার্স্ট নিরাপদ সিস্টেম",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "আইএসপি বিলিং কন্ট্রোল রুম",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "গ্রাহক ব্যবস্থাপনা, পেমেন্ট ও cPanel হোস্টিং সিঙ্ক",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Server JSON Banner Ad (GIF / Image support)
            if (bannerAds.isNotEmpty()) {
                item {
                    BannerAdCard(
                        ads = bannerAds,
                        position = "dashboard"
                    )
                }
            }

            // Stat Cards Grid
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatMetricCard(
                        title = "মোট গ্রাহক",
                        value = "${stats.totalCustomers}",
                        icon = Icons.Default.People,
                        iconTint = PrimaryBlue,
                        backgroundColor = PrimaryBlueContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCustomers
                    )
                    StatMetricCard(
                        title = "সক্রিয় লাইন",
                        value = "${stats.activeCustomers}",
                        icon = Icons.Default.CheckCircle,
                        iconTint = StatusActive,
                        backgroundColor = StatusActiveContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCustomers
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatMetricCard(
                        title = "মেয়াদউত্তীর্ণ লাইন",
                        value = "${stats.expiredCustomers}",
                        icon = Icons.Default.Warning,
                        iconTint = StatusExpired,
                        backgroundColor = StatusExpiredContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCustomers
                    )
                    StatMetricCard(
                        title = "মোট বকেয়া বিল",
                        value = CurrencyUtils.formatCurrency(stats.totalDue),
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = StatusDue,
                        backgroundColor = StatusDueContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCustomers
                    )
                }
            }

            // Collection Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "কালেকশন সামারি",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = StatusActive
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CollectionMetric(
                                label = "আজকের আদায়",
                                amount = CurrencyUtils.formatCurrency(stats.todayCollection),
                                color = StatusActive
                            )
                            Divider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                            CollectionMetric(
                                label = "চলতি মাসের আদায়",
                                amount = CurrencyUtils.formatCurrency(stats.monthlyCollection),
                                color = PrimaryBlue
                            )
                            Divider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                            CollectionMetric(
                                label = "সর্বমোট আয়",
                                amount = CurrencyUtils.formatCurrency(stats.totalIncome),
                                color = AccentPurple
                            )
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Column {
                    Text(
                        text = "কুইক অ্যাকশন",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton(
                            label = "নতুন গ্রাহক",
                            icon = Icons.Default.PersonAdd,
                            color = PrimaryBlue,
                            onClick = onNavigateToAddCustomer
                        )
                        QuickActionButton(
                            label = "প্যাকেজসমূহ",
                            icon = Icons.Default.Speed,
                            color = SecondaryTeal,
                            onClick = onNavigateToPackages
                        )
                        QuickActionButton(
                            label = "হিসাব-রিপোর্ট",
                            icon = Icons.Default.Assessment,
                            color = AccentPurple,
                            onClick = onNavigateToReports
                        )
                        QuickActionButton(
                            label = "সার্ভার সিঙ্ক",
                            icon = Icons.Default.CloudSync,
                            color = StatusActive,
                            onClick = onTriggerSync
                        )
                    }
                }
            }

            // Recent Customers List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সাম্প্রতিক গ্রাহকবৃন্দ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onNavigateToCustomers) {
                        Text("সবগুলো দেখুন")
                    }
                }
            }

            if (recentCustomers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "এখনো কোনো গ্রাহক যোগ করা হয়নি। 'নতুন গ্রাহক' বাটনে ট্যাপ করে যোগ করুন।",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(recentCustomers.take(5)) { customer ->
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

@Composable
fun CustomerCardItemBangla(
    customer: CustomerEntity,
    onClick: () -> Unit,
    onPayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = customer.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = customer.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusBadgeBangla(status = customer.connectionStatus)
                    }
                    Text(
                        text = "${customer.customerId} • ${customer.packageName} (${customer.packageSpeedMbps} Mbps)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "এলাকা: ${customer.area.ifBlank { "সাধারণ" }} • ফোন: ${customer.mobileNumber}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (customer.dueAmount > 0) {
                    Text(
                        text = "বকেয়া: ${CurrencyUtils.formatCurrency(customer.dueAmount)}",
                        fontWeight = FontWeight.Bold,
                        color = StatusExpired,
                        fontSize = 13.sp
                    )
                } else {
                    Text(
                        text = "পরিশোধিত",
                        fontWeight = FontWeight.Bold,
                        color = StatusActive,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onPayClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("পেমেন্ট", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun StatusBadgeBangla(status: String) {
    val (label, bgColor, textColor) = when (status) {
        "ACTIVE" -> Triple("সক্রিয়", StatusActiveContainer, StatusActive)
        "EXPIRED" -> Triple("মেয়াদউত্তীর্ণ", StatusExpiredContainer, StatusExpired)
        else -> Triple("স্থগিত", StatusDueContainer, StatusDue)
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun CollectionMetric(label: String, amount: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
