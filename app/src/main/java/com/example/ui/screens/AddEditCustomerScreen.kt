package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CustomerEntity
import com.example.data.models.PackageEntity
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomerScreen(
    existingCustomer: CustomerEntity?,
    packages: List<PackageEntity>,
    onBackClick: () -> Unit,
    onSaveCustomer: (CustomerEntity) -> Unit
) {
    var customerId by remember { mutableStateOf(existingCustomer?.customerId ?: "ISP-${(1000..9999).random()}") }
    var name by remember { mutableStateOf(existingCustomer?.name ?: "") }
    var mobileNumber by remember { mutableStateOf(existingCustomer?.mobileNumber ?: "") }
    var altMobileNumber by remember { mutableStateOf(existingCustomer?.altMobileNumber ?: "") }
    var address by remember { mutableStateOf(existingCustomer?.address ?: "") }
    var area by remember { mutableStateOf(existingCustomer?.area ?: "") }
    var selectedPackage by remember { mutableStateOf(existingCustomer?.packageName ?: packages.firstOrNull()?.name ?: "২০ এমবিপিএস স্ট্যান্ডার্ড") }
    var speedMbps by remember { mutableStateOf((existingCustomer?.packageSpeedMbps ?: packages.firstOrNull()?.speedMbps ?: 20).toString()) }
    var monthlyBill by remember { mutableStateOf((existingCustomer?.monthlyBill ?: packages.firstOrNull()?.monthlyPrice ?: 800.0).toString()) }
    var installationCharge by remember { mutableStateOf((existingCustomer?.installationCharge ?: 0.0).toString()) }
    var billingDate by remember { mutableStateOf((existingCustomer?.billingDate ?: 5).toString()) }
    var remarks by remember { mutableStateOf(existingCustomer?.remarks ?: "") }

    var expandedPackageMenu by remember { mutableStateOf(false) }

    fun handleSave() {
        if (name.isBlank() || mobileNumber.isBlank()) return

        val now = System.currentTimeMillis()
        val customer = (existingCustomer ?: CustomerEntity(
            customerId = customerId,
            name = name,
            mobileNumber = mobileNumber
        )).copy(
            customerId = customerId,
            name = name,
            mobileNumber = mobileNumber,
            altMobileNumber = altMobileNumber,
            address = address,
            area = area,
            packageName = selectedPackage,
            packageSpeedMbps = speedMbps.toIntOrNull() ?: 20,
            monthlyBill = monthlyBill.toDoubleOrNull() ?: 800.0,
            installationCharge = installationCharge.toDoubleOrNull() ?: 0.0,
            billingDate = billingDate.toIntOrNull()?.coerceIn(1, 31) ?: 5,
            remarks = remarks,
            updatedAt = now
        )
        onSaveCustomer(customer)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingCustomer == null) "নতুন গ্রাহক যুক্ত করুন" else "গ্রাহক তথ্য আপডেট", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { handleSave() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_customer_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("গ্রাহক তথ্য সেভ করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("ব্যক্তিগত ও কন্টাক্ট ডিটেইলস", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            item {
                OutlinedTextField(
                    value = customerId,
                    onValueChange = { customerId = it },
                    label = { Text("গ্রাহক আইডি (Customer ID) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("গ্রাহকের নাম *") },
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_name"),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = { Text("মোবাইল নম্বর *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_phone"),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = altMobileNumber,
                    onValueChange = { altMobileNumber = it },
                    label = { Text("বিকল্প মোবাইল নম্বর") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("সম্পূর্ণ ঠিকানা") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("এলাকা / জোন (যেমন: গুলশান, উত্তরা)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Text("প্যাকেজ ও কানেকশন সেটিংস", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedPackage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("ইন্টারনেট প্যাকেজ নির্বাচন করুন") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().clickable { expandedPackageMenu = true }
                    )
                    DropdownMenu(
                        expanded = expandedPackageMenu,
                        onDismissRequest = { expandedPackageMenu = false }
                    ) {
                        packages.forEach { pkg ->
                            DropdownMenuItem(
                                text = { Text("${pkg.name} (${pkg.speedMbps} Mbps) - ৳${pkg.monthlyPrice.toInt()}") },
                                onClick = {
                                    selectedPackage = pkg.name
                                    speedMbps = pkg.speedMbps.toString()
                                    monthlyBill = pkg.monthlyPrice.toString()
                                    expandedPackageMenu = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = speedMbps,
                        onValueChange = { speedMbps = it },
                        label = { Text("স্পিড (Mbps)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = monthlyBill,
                        onValueChange = { monthlyBill = it },
                        label = { Text("মাসিক বিল (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = billingDate,
                        onValueChange = { billingDate = it },
                        label = { Text("বিলিং তারিখ (১-৩১)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = installationCharge,
                        onValueChange = { installationCharge = it },
                        label = { Text("কানেকশন চার্জ (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("মন্তব্য / নোট") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
