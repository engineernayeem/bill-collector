package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.SettingsEntity
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.StatusActive
import com.example.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: SettingsEntity,
    isSyncing: Boolean,
    syncMessage: String?,
    onBackClick: () -> Unit,
    onSaveSettings: (SettingsEntity) -> Unit,
    onTriggerSync: () -> Unit,
    onExportJson: ((String) -> Unit) -> Unit,
    onImportJson: (String, (Boolean) -> Unit) -> Unit,
    onClearSyncMessage: () -> Unit,
    onCheckUpdate: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current

    var serverUrl by remember(settings) { mutableStateOf(settings.serverUrl) }
    var apiKey by remember(settings) { mutableStateOf(settings.apiKey) }
    var reminderDays by remember(settings) { mutableStateOf(settings.reminderDays) }
    var pinEnabled by remember(settings) { mutableStateOf(settings.pinEnabled) }
    var pinCode by remember(settings) { mutableStateOf(settings.pinCode) }
    var autoSync by remember(settings) { mutableStateOf(settings.autoSync) }
    var darkMode by remember(settings) { mutableStateOf(settings.darkMode) }
    var oneSignalAppId by remember(settings) { mutableStateOf(settings.oneSignalAppId) }

    var showImportDialog by remember { mutableStateOf(false) }
    var jsonImportText by remember { mutableStateOf("") }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    LaunchedEffect(syncMessage) {
        if (!syncMessage.isNull_OrEmpty()) {
            Toast.makeText(context, syncMessage, Toast.LENGTH_LONG).show()
            onClearSyncMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("অ্যাপ সেটিংস ও সার্ভার সিঙ্ক", fontWeight = FontWeight.Bold) },
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
            // Google Account Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("গুগল ও ব্যবহারকারী অ্যাকাউন্ট", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            AssistChip(
                                onClick = onNavigateToLogin,
                                label = { Text("সাইন-ইন / পরিবর্তন", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp)) }
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "বর্তমান অ্যাক্টিভ ইমেইল:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = settings.userEmail.ifBlank { "nayeemmallik801@gmail.com" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onNavigateToLogin,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google Auth দিয়ে নতুন ইমেইলে সাইন-ইন করুন")
                        }
                    }
                }
            }

            // Server & Sync Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("সার্ভার ডাটা সিঙ্ক সেটিংস", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = PrimaryBlue)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("অটো ব্যাকগ্রাউন্ড সিঙ্ক", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text("ইন্টারনেট থাকলে ব্যাকগ্রাউন্ডে বিল ও কাস্টমার ডাটা অটো সিঙ্ক হবে", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = autoSync,
                                onCheckedChange = {
                                    autoSync = it
                                    onSaveSettings(settings.copy(autoSync = it))
                                }
                            )
                        }

                        if (settings.lastSyncTime > 0) {
                            Text(
                                text = "সর্বশেষ সফল সিঙ্ক: ${DateUtils.formatDateTime(settings.lastSyncTime)}",
                                fontSize = 12.sp,
                                color = StatusActive,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = {
                                onSaveSettings(settings.copy(autoSync = autoSync))
                                onTriggerSync()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("sync_now_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("সার্ভারে তথ্য আপলোড/ডাউনলোড হচ্ছে...")
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("এখনই সার্ভারের সাথে সিঙ্ক করুন")
                            }
                        }

                        OutlinedButton(
                            onClick = onCheckUpdate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("অ্যাপের নতুন আপডেট চেক করুন")
                        }
                    }
                }
            }

            // Notification Reminders Settings
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
                        Text("বিল রিমাইন্ডার ও অ্যালার্ট সেটিংস", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("বিল পরিশোধের তারিখের কতদিন পূর্বে রিমাইন্ডার অ্যালার্ট পাবেন:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        val reminderOptions = listOf(7 to "৭ দিন পূর্বে", 3 to "৩ দিন পূর্বে", 1 to "১ দিন পূর্বে", 0 to "বিলিংয়ের দিন")
                        reminderOptions.forEach { (days, label) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = reminderDays == days,
                                    onClick = {
                                        reminderDays = days
                                        onSaveSettings(settings.copy(reminderDays = days))
                                    }
                                )
                                Text(label, fontSize = 14.sp)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        Button(
                            onClick = {
                                com.example.notifications.BillReminderManager(context).sendNotification(
                                    title = "🔔 টেস্ট বিল অ্যালার্ট ও নটিফিকেশন",
                                    message = "নটিফিকেশন এবং কাস্টমার বিলিং অ্যালার্ট সিস্টেম সঠিকভাবে কাজ করছে।"
                                )
                                Toast.makeText(context, "টেস্ট নটিফিকেশন পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("টেস্ট বিল অ্যালার্ট পাঠান")
                        }
                    }
                }
            }

            // Backup & Restore JSON Data
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
                        Text("JSON ডাটা ব্যাকআপ ও রিস্টোর", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("আপনার সকল ডাটাবেজ ব্যাকআপ JSON ফরম্যাটে শেয়ার বা ইম্পোর্ট করুন:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onExportJson { json ->
                                        exportedJsonText = json
                                        showExportDialog = true
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("export_json_btn")
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("এক্সপোর্ট JSON")
                            }

                            OutlinedButton(
                                onClick = { showImportDialog = true },
                                modifier = Modifier.weight(1f).testTag("import_json_btn")
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ইম্পোর্ট JSON")
                            }
                        }
                    }
                }
            }

            // App Security & PIN Lock
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
                        Text("সিকিউরিটি ও পিন লক", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("পিন লক চালু করুন", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text("অ্যাপে প্রবেশের সময় ৪ ডিজিটের পিন আবশ্যক হবে", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = pinEnabled,
                                onCheckedChange = {
                                    pinEnabled = it
                                    onSaveSettings(settings.copy(pinEnabled = it))
                                }
                            )
                        }

                        if (pinEnabled) {
                            OutlinedTextField(
                                value = pinCode,
                                onValueChange = {
                                    pinCode = it
                                    onSaveSettings(settings.copy(pinCode = it))
                                },
                                label = { Text("৪ ডিজিটের সিকিউরিটি পিন (PIN)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("এক্সপোর্টকৃত JSON ডাটাবেজ") },
            text = {
                OutlinedTextField(
                    value = exportedJsonText,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().height(240.dp)
                )
            },
            confirmButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("JSON ব্যাকআপ রিস্টোর করুন") },
            text = {
                OutlinedTextField(
                    value = jsonImportText,
                    onValueChange = { jsonImportText = it },
                    placeholder = { Text("এখানে ব্যাকআপ JSON পেস্ট করুন...") },
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (jsonImportText.isBlank()) return@Button
                    onImportJson(jsonImportText) { success ->
                        showImportDialog = false
                        Toast.makeText(
                            context,
                            if (success) "ডাটাবেজ সফলভাবে রিস্টোর হয়েছে!" else "অকার্যকর JSON ব্যাকআপ ফরম্যাট।",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text("রিস্টোর করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

private fun String?.isNull_OrEmpty(): Boolean = this == null || this.trim().isEmpty()
