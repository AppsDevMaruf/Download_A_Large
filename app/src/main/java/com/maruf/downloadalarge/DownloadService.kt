package com.maruf.downloadalarge

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf

 class DownloadService : Service() {

     private val notificationManager: NotificationManager by lazy {
         getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
     }
    private lateinit var notificationChannel: NotificationChannel

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = "https://drive.google.com/file/d/0B8vtjkRL-9A_aldKWUhOOWNua0k/view?usp=sharing&resourcekey=0-JpmBH9xhtB5VEFmHPv-Rkw"
        startForeground(NOTIFICATION_ID, createNotification(0))
        downloadFile(url)
        return START_STICKY
    }

     override fun onBind(p0: Intent?): IBinder? {
         TODO("Not yet implemented")
     }

     private fun downloadFile(url: String) {
        val workManager = WorkManager.getInstance(applicationContext)
        val downloadRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .setInputData(workDataOf("url" to url))
            .build()
        workManager.enqueue(downloadRequest)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(CHANNEL_ID, "Download Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification(progress: Int): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading File")
            .setContentText("$progress%")
            .setSmallIcon(R.drawable.round_download_24)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun showDownloadNotification(progress: Int) {
        val notification = createNotification(progress)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1
    }
}
