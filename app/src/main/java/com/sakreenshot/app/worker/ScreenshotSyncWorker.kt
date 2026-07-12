package com.sakreenshot.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sakreenshot.app.data.db.AppDatabase
import com.sakreenshot.app.data.media.MediaStoreHelper

class ScreenshotSyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(appContext)
        val dao = database.screenshotDao()
        val mediaStoreHelper = MediaStoreHelper(appContext)
        val workManager = WorkManager.getInstance(appContext)

        try {
            val screenshots = mediaStoreHelper.queryScreenshots()
            
            for (item in screenshots) {
                // Check if already indexed
                val existing = dao.findByMediaStoreId(item.id)
                if (existing != null) {
                    continue // Skip already processed
                }

                // Enqueue unique job per item
                val inputData = Data.Builder()
                    .putLong("MEDIA_STORE_ID", item.id)
                    .putString("URI", item.uri.toString())
                    .putString("DISPLAY_NAME", item.displayName)
                    .putString("RELATIVE_PATH", item.relativePath)
                    .putLong("DATE_ADDED", item.dateAdded)
                    .putLong("FILE_SIZE", item.fileSize)
                    .putInt("WIDTH", item.width)
                    .putInt("HEIGHT", item.height)
                    .build()

                val request = OneTimeWorkRequestBuilder<ProcessItemWorker>()
                    .setInputData(inputData)
                    .build()

                workManager.enqueueUniqueWork(
                    "process_screenshot_${item.id}",
                    ExistingWorkPolicy.KEEP,
                    request
                )
            }

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}
