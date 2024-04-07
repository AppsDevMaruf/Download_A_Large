import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.maruf.downloadalarge.R

class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize notification manager
        notificationManager = NotificationManagerCompat.from(this)

        // Create notification channel
        createNotificationChannel()

        // Find views
        val downloadButton = findViewById<Button>(R.id.download_button)
        val progressView = findViewById<ProgressBar>(R.id.progress_bar)

        // Start download service when button is clicked
        downloadButton.setOnClickListener {
            startDownloadService()
        }
    }

    override fun onResume() {
        super.onResume()
        // Cancel the notification when app is resumed
        notificationManager.cancel(DownloadService.NOTIFICATION_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Download Channel"
            val descriptionText = "Channel for download notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(DownloadService.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startDownloadService() {
        val downloadIntent = Intent(this, DownloadService::class.java)
        startService(downloadIntent)
    }
}
