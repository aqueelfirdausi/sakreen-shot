package com.sakreenshot.app.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sakreenshot.app.data.classification.TextClassifier
import com.sakreenshot.app.data.db.AppDatabase
import com.sakreenshot.app.data.db.ScreenshotEntity
import com.sakreenshot.app.data.ocr.TextExtractor

class ProcessItemWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val mediaStoreId = inputData.getLong("MEDIA_STORE_ID", -1L)
        val uriString = inputData.getString("URI")
        val displayName = inputData.getString("DISPLAY_NAME") ?: ""
        val relativePath = inputData.getString("RELATIVE_PATH")
        val dateAdded = inputData.getLong("DATE_ADDED", 0L)
        val fileSize = inputData.getLong("FILE_SIZE", 0L)
        val width = inputData.getInt("WIDTH", 0)
        val height = inputData.getInt("HEIGHT", 0)

        if (mediaStoreId == -1L || uriString == null) return Result.failure()

        val database = AppDatabase.getDatabase(appContext)
        val dao = database.screenshotDao()

        if (runAttemptCount > 3) {
            val failedEntity = ScreenshotEntity(
                contentUri = uriString,
                mediaStoreId = mediaStoreId,
                displayName = displayName,
                relativePath = relativePath,
                extractedText = "",
                normalizedText = "",
                primaryCategory = "UNSORTED", // fallback
                classificationScore = 0,
                capturedAt = dateAdded,
                indexedAt = System.currentTimeMillis(),
                modifiedAt = System.currentTimeMillis(),
                width = width,
                height = height,
                fileSize = fileSize,
                isPinned = false,
                estimatedExpiry = null,
                processingStatus = "FAILED_OCR",
                contentHash = null
            )
            dao.insertOrUpdate(failedEntity)
            return Result.failure()
        }
        val textExtractor = TextExtractor(appContext)
        val textClassifier = TextClassifier()

        return try {
            // Check if already exists just in case
            val existing = dao.findByMediaStoreId(mediaStoreId)
            if (existing != null) {
                return Result.success()
            }

            val uri = Uri.parse(uriString)
            val extractedText = textExtractor.extractText(uri)
            val normalizedText = extractedText.lowercase().replace(Regex("[^a-z0-9 ]"), "")
            val category = textClassifier.classify(extractedText)

            val entity = ScreenshotEntity(
                contentUri = uriString,
                mediaStoreId = mediaStoreId,
                displayName = displayName,
                relativePath = relativePath,
                extractedText = extractedText,
                normalizedText = normalizedText,
                primaryCategory = category.name,
                classificationScore = 100,
                capturedAt = dateAdded,
                indexedAt = System.currentTimeMillis(),
                modifiedAt = System.currentTimeMillis(),
                width = width,
                height = height,
                fileSize = fileSize,
                isPinned = false,
                estimatedExpiry = null,
                processingStatus = "DONE",
                contentHash = null
            )

            dao.insertOrUpdate(entity)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
