 package com.maruf.downloadalarge

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maruf.downloadalarge.databinding.ActivityMainBinding


 class MainActivity : AppCompatActivity() {
     private lateinit var downloadManager: DownloadManager
     private var downloadId: Long = -1
     private var _binding: ActivityMainBinding? = null
     private val binding get() = _binding!!


     @SuppressLint("SuspiciousIndentation")
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         _binding = ActivityMainBinding.inflate(layoutInflater)
         setContentView(binding.root)

     downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

         // Button click listener to start the download
         binding.downloadBtn.setOnClickListener {
             startDownload()
         }
     } private fun startDownload() {
         val request = DownloadManager.Request(Uri.parse("https://drive.google.com/file/d/0B8vtjkRL-9A_d1Bmc0Q0eGY2Y3c/view?usp=sharing&resourcekey=0-s5-E2nV8L8oCmVoLda8iCg"))
             .setTitle("Sample Video")
             .setDescription("Downloading")
             .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
             .setRequiresCharging(false)
             .setAllowedOverMetered(true)
             .setAllowedOverRoaming(true)

         downloadId = downloadManager.enqueue(request)
     }

     override fun onResume() {
         super.onResume()
         // Check download status and update UI
         checkDownloadStatus()
     }

     @SuppressLint("Range")
     private fun checkDownloadStatus() {
         val query = DownloadManager.Query().setFilterById(downloadId)
         val cursor = downloadManager.query(query)

         if (cursor.moveToFirst()) {
             val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
             val progress = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
             val total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

             if (status == DownloadManager.STATUS_SUCCESSFUL) {
                 // Download complete
                 binding.textViewProgress.text = "Download Complete"
             } else if (status == DownloadManager.STATUS_FAILED) {
                 // Download failed
                 binding.textViewProgress.text= "Download Failed"
             } else {
                 // Download in progress
                 val percent = (progress * 100 / total).toInt()
                 binding.textViewProgress.text = "Downloading... $percent%"
             }
         }

         cursor.close()

 }


     override fun onDestroy() {
         super.onDestroy()
         _binding = null
     }
 }