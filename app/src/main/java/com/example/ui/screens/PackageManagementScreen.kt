package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PackageEntity
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.StatusExpired
import com.example.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageManagementScreen(
    packages: List<PackageEntity>,
    onBackClick: () -> Unit,
    onSavePackage: (PackageEntity) -> Unit,
    onDeletePackage: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingPackage by remember { mutableStateOf<PackageEntity?>(null) }

    fun openAddDialog() {
        editingPackage = null
        showDialog = true
    }

    fun openEditDialog(pkg: PackageEntity) {
        editingPackage = pkg
        showDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ইন্টারনেট প্যাকেজসমূহ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openAddDialog() },
                containerColor = PrimaryBlue,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_package_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "প্যাকেজ যোগ করুন")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "সেটআপ করা সকল ব্যান্ডউইথ প্ল্যান",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(packages) { pkg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryTeal.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = SecondaryTeal,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = pkg.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${pkg.speedMbps} Mbps স্পিড • ${pkg.description.ifBlank { "স্ট্যান্ডার্ড প্ল্যান" }}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = CurrencyUtils.formatCurrency(pkg.monthlyPrice),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PrimaryBlue
                            )
                            Row {
                                IconButton(onClick = { openEditDialog(pkg) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "সম্পাদনা", modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onDeletePackage(pkg.id) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "ডিলিট", tint = StatusExpired, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        PackageDialog(
            existingPackage = editingPackage,
            onDismiss = { showDialog = false },
            onSave = { pkg ->
                onSavePackage(pkg)
                showDialog = false
            }
        )
    }
}

@Composable
fun PackageDialog(
    existingPackage: PackageEntity?,
    onDismiss: () -> Unit,
    onSave: (PackageEntity) -> Unit
) {
    var name by remember { mutableStateOf(existingPackage?.name ?: "") }
    var speed by remember { mutableStateOf((existingPackage?.speedMbps ?: 20).toString()) }
    var price by remember { mutableStateOf((existingPackage?.monthlyPrice ?: 800.0).toString()) }
    var description by remember { mutableStateOf(existingPackage?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingPackage == null) "নতুন ইন্টারনেট প্যাকেজ" else "প্যাকেজ পরিবর্তন করুন") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("প্যাকেজের নাম (যেমন: ২০ এমবিপিএস আল্ট্রা)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = speed,
                    onValueChange = { speed = it },
                    label = { Text("স্পিড (Mbps)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("মাসিক মূল্য (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("বিবরণ") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank()) return@Button
                val pkg = (existingPackage ?: PackageEntity(
                    name = name,
                    speedMbps = speed.toIntOrNull() ?: 20,
                    monthlyPrice = price.toDoubleOrNull() ?: 800.0
                )).copy(
                    name = name,
                    speedMbps = speed.toIntOrNull() ?: 20,
                    monthlyPrice = price.toDoubleOrNull() ?: 800.0,
                    description = description,
                    updatedAt = System.currentTimeMillis()
                )
                onSave(pkg)
            }) {
                Text("সেভ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
