package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object AppUpdateInstaller {

    suspend fun downloadAndInstallApk(
        context: Context,
        apkUrl: String,
        onProgress: (Int) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = URL(apkUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 30000
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(Exception("সার্ভার প্রতিক্রিয়া ব্যর্থ: ${connection.responseCode}"))
            }

            val fileLength = connection.contentLength
            val apkFile = File(context.externalCacheDir ?: context.cacheDir, "update_app.apk")
            if (apkFile.exists()) {
                apkFile.delete()
            }

            val input = connection.inputStream
            val output = FileOutputStream(apkFile)

            val data = ByteArray(8192)
            var total: Long = 0
            var count: Int
            var lastReportedProgress = -1

            while (input.read(data).also { count = it } != -1) {
                total += count
                output.write(data, 0, count)

                if (fileLength > 0) {
                    val progress = ((total * 100) / fileLength).toInt()
                    if (progress != lastReportedProgress) {
                        lastReportedProgress = progress
                        onProgress(progress)
                    }
                }
            }

            output.flush()
            output.close()
            input.close()

            withContext(Dispatchers.Main) {
                installApk(context, apkFile)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun installApk(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }
}
