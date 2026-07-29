package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CustomerEntity
import com.example.data.models.PaymentEntity
import com.example.ui.theme.*
import com.example.utils.CurrencyUtils
import com.example.utils.PdfReceiptGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    customer: CustomerEntity?,
    onBackClick: () -> Unit,
    onConfirmPayment: (customerId: String, amount: Double, method: String, note: String, onSuccess: (PaymentEntity) -> Unit) -> Unit
) {
    val context = LocalContext.current

    if (customer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var paidAmountText by remember { mutableStateOf(customer.monthlyBill.toString()) }
    var selectedMethod by remember { mutableStateOf("Bkash") }
    var note by remember { mutableStateOf("মাসিক ইন্টারনেট বিল প্রদান") }

    val methods = listOf("Bkash", "Cash", "Nagad", "Rocket", "Bank", "Other")

    val paidAmount = paidAmountText.toDoubleOrNull() ?: 0.0
    val totalOwed = customer.dueAmount + customer.monthlyBill
    val remainingDue = (totalOwed - paidAmount).coerceAtLeast(0.0)
    val advanceAmount = (paidAmount - totalOwed).coerceAtLeast(0.0)

    fun getMethodLabel(m: String): String {
        return when(m) {
            "Cash" -> "ক্যাশ"
            "Bkash" -> "বিকাশ"
            "Nagad" -> "নগদ"
            "Rocket" -> "রকেট"
            "Bank" -> "ব্যাংক"
            else -> "অন্যান্য"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("বিল পেমেন্ট গ্রহণ করুন", fontWeight = FontWeight.Bold) },
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
                        onClick = {
                            if (paidAmount <= 0) return@Button
                            onConfirmPayment(
                                customer.id,
                                paidAmount,
                                selectedMethod,
                                note
                            ) { payment ->
                                val receiptText = PdfReceiptGenerator.generateTextReceipt(payment, customer)
                                PdfReceiptGenerator.shareReceipt(context, receiptText, customer.mobileNumber)
                                onBackClick()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_payment_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusActive)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("পেমেন্ট নিশ্চিত করুন ও রিসিট দিন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Header Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlueContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = customer.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlueDark
                        )
                        Text(
                            text = "আইডি: ${customer.customerId} • ${customer.packageName} (${customer.packageSpeedMbps} Mbps)",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("পূর্ববর্তী বকেয়া", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(CurrencyUtils.formatCurrency(customer.dueAmount), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("চলতি বিল", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(CurrencyUtils.formatCurrency(customer.monthlyBill), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("মোট পাওনা", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(CurrencyUtils.formatCurrency(totalOwed), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusExpired)
                            }
                        }
                    }
                }
            }

            // Payment Input Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("আদায়কৃত অর্থ ও মাধ্যম", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        OutlinedTextField(
                            value = paidAmountText,
                            onValueChange = { paidAmountText = it },
                            label = { Text("পরিশোধিত টাকা (৳)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("paid_amount_input"),
                            singleLine = true
                        )

                        Text("পেমেন্টের মাধ্যম", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(methods) { method ->
                                FilterChip(
                                    selected = selectedMethod == method,
                                    onClick = { selectedMethod = method },
                                    label = { Text(getMethodLabel(method)) },
                                    leadingIcon = if (selectedMethod == method) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("পেমেন্ট নোট / রেফারেন্স") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Live Balance Preview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("আপডেটেড ব্যালেন্স পূর্বরূপ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("নতুন অবশিষ্ট বকেয়া", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(CurrencyUtils.formatCurrency(remainingDue), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (remainingDue > 0) StatusExpired else StatusActive)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("নতুন অগ্রিম জমা", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(CurrencyUtils.formatCurrency(advanceAmount), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }
                }
            }
        }
    }
}
