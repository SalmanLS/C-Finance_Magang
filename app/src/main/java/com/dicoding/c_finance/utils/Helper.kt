package com.dicoding.c_finance.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DecimalFormat
import java.util.Locale
import java.text.SimpleDateFormat

//class DownloadNotificationHelper(private val context: Context) {
//
//    companion object {
//        private const val CHANNEL_ID = "download_channel"
//        private const val NOTIFICATION_ID = 1
//    }
//
//    init {
//        createNotificationChannel()
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "Download Notifications"
//            val descriptionText = "Notifications for completed downloads"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            val notificationManager: NotificationManager =
//                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    fun showDownloadCompletedNotification(filePath: String) {
//        val fileUri = Uri.parse(filePath)
//
//        val openFileIntent = Intent(Intent.ACTION_VIEW).apply {
//            setDataAndType(fileUri, "resource/folder")
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
//        }
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            openFileIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(android.R.drawable.stat_sys_download_done)
//            .setContentTitle("Download Complete")
//            .setContentText("File downloaded successfully.")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        with(NotificationManagerCompat.from(context)) {
//            notify(NOTIFICATION_ID, builder.build())
//        }
//    }
//}

data class UserInputFields(
    val username: String,
    val password: String,
    val phone: String,
    val nama: String
)

fun customDateFormat(sqlDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    val parsedDate = inputFormat.parse(sqlDate)
    return parsedDate?.let { outputFormat.format(it) } ?: "Invalid Date"
}

fun customCurrencyFormat(amount: Double, currencySymbol: String = "Rp"): String {
    val formatter = DecimalFormat("#,###.##")
    return "$currencySymbol ${formatter.format(amount)}"
}

fun formatTimestamp(inputTimestamp: String, inputFormat: String, outputFormat: String): String {
    return try {
        val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
        val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())

        val date = inputDateFormat.parse(inputTimestamp)
        outputDateFormat.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
        "Invalid date"
    }
}

fun saveFileToDownloads(body: ResponseBody, fileName: String): File? {
    return try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            val fileReader = ByteArray(4096)
            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0

            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)

            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) break

                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read
            }

            outputStream.flush()
            file
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val data: ResponseBody) : ExportState()
    data class Failure(val errorMessage: String) : ExportState()
}




