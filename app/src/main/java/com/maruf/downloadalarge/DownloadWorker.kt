import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val url = inputData.getString("url") ?: return Result.failure()

        val urlObject = URL(url)
        val path = urlObject.path
        val fileName = path.substring(path.lastIndexOf('/') + 1)
        val file = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

        val request = withContext(Dispatchers.IO) {
            URL(url).openStream()
        }
        val buffer = ByteArray(1024)
        var downloaded = 0L
        var totalLength: Long = 0

        val connection = (request as HttpURLConnection)
        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            return Result.failure()
        }

        totalLength = connection.contentLength.toLong()

        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }
        var bytesRead = withContext(Dispatchers.IO) {
            request.read(buffer)
        }
        while (bytesRead > 0) {
            withContext(Dispatchers.IO) {
                outputStream.write(buffer, 0, bytesRead)
            }
            downloaded += bytesRead
            // Send progress updates to UI component here
            bytesRead = withContext(Dispatchers.IO) {
                request.read(buffer)
            }
        }

        withContext(Dispatchers.IO) {
            outputStream.close()
        }
        return Result.success()
    }
}
