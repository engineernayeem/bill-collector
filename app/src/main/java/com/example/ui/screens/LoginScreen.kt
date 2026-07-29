package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.StatusExpired

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    currentSavedEmail: String = "nayeemmallik801@gmail.com",
    onEmailLogin: (String, String) -> Boolean,
    onSuccessLogin: () -> Unit
) {
    var email by remember { mutableStateOf(currentSavedEmail.ifBlank { "nayeemmallik801@gmail.com" }) }
    var password by remember { mutableStateOf("123456") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleLogin() {
        if (email.isBlank() || !email.contains("@")) {
            errorMessage = "অনুগ্রহ করে একটি সঠিক ইমেইল ঠিকানা দিন"
            return
        }
        if (password.length < 4) {
            errorMessage = "পাসওয়ার্ড অন্তত ৪ অক্ষরের হতে হবে"
            return
        }
        if (onEmailLogin(email.trim(), password.trim())) {
            onSuccessLogin()
        } else {
            errorMessage = "লগইন ব্যর্থ হয়েছে। আবার চেষ্টা করুন।"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "বিল কালেক্টর - সাইন ইন",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "আপনার ইমেইল দিয়ে লগইন করুন (আলাদা ডাটা)",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("ইমেইল ঠিকানা (Email)") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryBlue)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("পাসওয়ার্ড / পিন (Password)") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue)
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage!!,
                        color = StatusExpired,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { handleLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        text = "লগইন করুন (Sign In)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "দ্রুত টেস্ট অ্যাকাউন্ট নির্বাচন করুন:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AssistChip(
                        onClick = {
                            email = "nayeemmallik801@gmail.com"
                            password = "123"
                        },
                        label = { Text("nayeemmallik801...", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                    AssistChip(
                        onClick = {
                            email = "user2@isp.com"
                            password = "123"
                        },
                        label = { Text("user2@isp.com", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }
            }
        }
    }
}
