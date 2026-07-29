package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.AppVersionDto
import com.example.ui.theme.PrimaryBlue

@Composable
fun UpdateDialog(
    updateInfo: AppVersionDto,
    isDownloading: Boolean,
    downloadProgress: Int,
    errorMessage: String?,
    onUpdateClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isDownloading && !updateInfo.forceUpdate) {
                onDismissClick()
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "নতুন আপডেট পাওয়া গেছে!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = "ভার্সন ${updateInfo.versionName} (Build ${updateInfo.versionCode})",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (updateInfo.releaseNotes.isNotBlank()) {
                    Text(
                        text = "আপডেটে নতুন কি আছে:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = updateInfo.releaseNotes,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AnimatedVisibility(visible = isDownloading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = PrimaryBlue,
                            trackColor = PrimaryBlue.copy(alpha = 0.2f)
                        )
                        Text(
                            text = "ডাউনলোড হচ্ছে... $downloadProgress%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }

                errorMessage?.takeIf { it.isNotBlank() }?.let { err ->
                    Text(
                        text = err,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onUpdateClick,
                enabled = !isDownloading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier.testTag("update_now_btn")
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (errorMessage != null) "পুনরায় চেষ্টা করুন" else "এখনই আপডেট করুন")
            }
        },
        dismissButton = {
            if (!updateInfo.forceUpdate && !isDownloading) {
                TextButton(onClick = onDismissClick) {
                    Text("পরে করবো")
                }
            }
        }
    )
}

private fun String?.isNull_OrBlank(): Boolean = this == null || this.trim().isEmpty()
