package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.DashboardStats
import com.example.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    stats: DashboardStats,
    onBackClick: () -> Unit
) {
    var selectedTimeframe by remember { mutableStateOf("মাসিক") }
    val timeframes = listOf("দৈনিক", "সাপ্তাহিক", "মাসিক", "বাৎসরিক")

    val estimatedWeekly = stats.monthlyCollection / 4.0
    val estimatedYearly = stats.totalIncome * 12.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("আইএসপি আর্থিক রিপোর্ট", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
            // Timeframe Selector Tab
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeframes.forEach { timeframe ->
                        FilterChip(
                            selected = selectedTimeframe == timeframe,
                            onClick = { selectedTimeframe = timeframe },
                            label = { Text(timeframe) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            // Financial Summary Card for Selected Timeframe
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
                                text = "$selectedTimeframe কালেকশন রিপোর্ট",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = PrimaryBlue)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val displayAmount = when (selectedTimeframe) {
                            "দৈনিক" -> stats.todayCollection
                            "সাপ্তাহিক" -> estimatedWeekly
                            "মাসিক" -> stats.monthlyCollection
                            else -> estimatedYearly
                        }

                        Text(
                            text = CurrencyUtils.formatCurrency(displayAmount),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "$selectedTimeframe সময়কালে মোট কালেকশন",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Visual Collection vs Due Bar Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "আদায় বনাম বকেয়ার তুলনাচিত্র",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Canvas Chart
                        val collectionVal = stats.monthlyCollection.toFloat()
                        val dueVal = stats.totalDue.toFloat()
                        val maxVal = maxOf(collectionVal, dueVal, 1.0f)

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            val barWidth = size.width * 0.25f
                            val chartHeight = size.height * 0.8f

                            val collectionHeight = (collectionVal / maxVal) * chartHeight
                            val dueHeight = (dueVal / maxVal) * chartHeight

                            // Draw Collection Bar (Green/Blue)
                            drawRoundRect(
                                color = PrimaryBlue,
                                topLeft = Offset(size.width * 0.2f - barWidth / 2, size.height - collectionHeight),
                                size = Size(barWidth, collectionHeight),
                                cornerRadius = CornerRadius(16f, 16f)
                            )

                            // Draw Due Bar (Red/Orange)
                            drawRoundRect(
                                color = StatusExpired,
                                topLeft = Offset(size.width * 0.8f - barWidth / 2, size.height - dueHeight),
                                size = Size(barWidth, dueHeight),
                                cornerRadius = CornerRadius(16f, 16f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(PrimaryBlue, shape = RoundedCornerShape(3.dp))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("মাসিক কালেকশন", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(StatusExpired, shape = RoundedCornerShape(3.dp))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("মোট বকেয়া", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Customer Status Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("গ্রাহক স্ট্যাটাস সামারি", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Divider()

                        ReportRow("মোট সক্রিয় গ্রাহক", "${stats.activeCustomers} জন", StatusActive)
                        ReportRow("মোট মেয়াদউত্তীর্ণ লাইন", "${stats.expiredCustomers} জন", StatusExpired)
                        ReportRow("সর্বমোট নিবন্ধিত গ্রাহক", "${stats.totalCustomers} জন", PrimaryBlue)
                        ReportRow("লাইফটাইম সর্বমোট আদায়", CurrencyUtils.formatCurrency(stats.totalIncome), AccentPurple)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
