package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CustomerEntity
import com.example.data.models.PaymentEntity
import com.example.ui.theme.*
import com.example.utils.CurrencyUtils
import com.example.utils.DateUtils
import com.example.utils.PdfReceiptGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customer: CustomerEntity?,
    payments: List<PaymentEntity>,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onRecordPaymentClick: (String) -> Unit
) {
    val context = LocalContext.current

    if (customer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    fun makeCall(phone: String) {
        if (phone.isBlank()) return
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    }

    fun sendSms(phone: String) {
        if (phone.isBlank()) return
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
            putExtra("sms_body", "প্রিয় ${customer.name}, আপনার আইএসপি ইন্টারনেট বিল বকেয়া: ${CurrencyUtils.formatCurrency(customer.dueAmount)}। ধন্যবাদ।")
        }
        context.startActivity(intent)
    }

    fun openWhatsApp(phone: String) {
        if (phone.isBlank()) return
        val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=প্রিয়%20${customer.name},%20আপনার%20আইএসপি%20বিল%20সংক্রান্ত%20মেসেজ।"))
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "হোয়াটসঅ্যাপ অ্যাপ পাওয়া যায়নি।", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(customer.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = "এডিট করুন")
                    }
                    IconButton(onClick = { onDeleteClick(customer.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "গ্রাহক মুছে ফেলুন", tint = StatusExpired)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { onRecordPaymentClick(customer.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("record_payment_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("পেমেন্ট গ্রহণ করুন (${CurrencyUtils.formatCurrency(customer.monthlyBill)})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            // Profile Card & Communications
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = customer.name.take(1).uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = customer.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "গ্রাহক আইডি: ${customer.customerId}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        StatusBadgeBangla(status = customer.connectionStatus)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Call, SMS, WhatsApp Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { makeCall(customer.mobileNumber) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("call_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusActiveContainer, contentColor = StatusActive)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "কল", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("কল করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { sendSms(customer.mobileNumber) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("sms_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlueContainer, contentColor = PrimaryBlue)
                            ) {
                                Icon(Icons.Default.Message, contentDescription = "এসএমএস", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("এসএমএস", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { openWhatsApp(customer.mobileNumber) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("whatsapp_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryTealContainer, contentColor = SecondaryTeal)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "হোয়াটসঅ্যাপ", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("হোয়াটসঅ্যাপ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Financial Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("বিলিং ও বকেয়া তথ্য", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailRowItem("মাসিক বিল", CurrencyUtils.formatCurrency(customer.monthlyBill))
                            DetailRowItem("বর্তমান বকেয়া", CurrencyUtils.formatCurrency(customer.dueAmount), isBold = true, color = if (customer.dueAmount > 0) StatusExpired else StatusActive)
                            DetailRowItem("অগ্রিম জমা", CurrencyUtils.formatCurrency(customer.advanceAmount))
                        }
                    }
                }
            }

            // Connection Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("সংযোগ ও প্যাকেজ তথ্য", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        InfoLine("প্যাকেজের নাম", customer.packageName)
                        InfoLine("ব্যান্ডউইথ স্পিড", "${customer.packageSpeedMbps} Mbps")
                        InfoLine("বিলিং তারিখ", "প্রতি মাসের ${customer.billingDate} তারিখ")
                        InfoLine("সর্বশেষ পেমেন্ট", DateUtils.formatDate(customer.lastPaymentDate))
                        InfoLine("পরবর্তী পেমেন্ট তারিখ", DateUtils.formatDate(customer.nextPaymentDate))
                        InfoLine("মেয়াদ উত্তীর্ণের তারিখ", DateUtils.formatDate(customer.expireDate))
                        InfoLine("কানেকশন চার্জ", CurrencyUtils.formatCurrency(customer.installationCharge))
                        InfoLine("ডিসকাউন্ট / ছাড়", CurrencyUtils.formatCurrency(customer.discount))
                    }
                }
            }

            // Address & Personal Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("ঠিকানা ও যোগাযোগ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        InfoLine("মোবাইল নম্বর", customer.mobileNumber)
                        InfoLine("বিকল্প নম্বর", customer.altMobileNumber.ifBlank { "নেই" })
                        InfoLine("সম্পূর্ণ ঠিকানা", customer.address.ifBlank { "দেওয়া হয়নি" })
                        InfoLine("এলাকা / জোন", customer.area.ifBlank { "সাধারণ জোন" })
                        InfoLine("মন্তব্য", customer.remarks.ifBlank { "কোনো বিশেষ মন্তব্য নেই" })
                    }
                }
            }

            // Payment History Header
            item {
                Text(
                    text = "পেমেন্ট ইতিহাস (${payments.size} টি)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (payments.isEmpty()) {
                item {
                    Text(
                        text = "এই গ্রাহকের কোনো পেমেন্ট রেকর্ড পাওয়া যায়নি।",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(payments) { payment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = payment.receiptNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "আদায়: ${CurrencyUtils.formatCurrency(payment.amount)} (মাধ্যম: ${payment.paymentMethod})",
                                    fontSize = 12.sp,
                                    color = StatusActive,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = DateUtils.formatDateTime(payment.paymentDate),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = {
                                val receiptText = PdfReceiptGenerator.generateTextReceipt(payment, customer)
                                PdfReceiptGenerator.shareReceipt(context, receiptText, customer.mobileNumber)
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "রিসিট শেয়ার করুন", tint = PrimaryBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRowItem(label: String, value: String, isBold: Boolean = false, color: Color = Color.Unspecified) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium, color = color)
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
