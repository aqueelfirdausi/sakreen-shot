package com.sakreenshot.app.worker

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object WorkManagerHelper {
    private const val SYNC_WORK_NAME = "ScreenshotSyncWork"
    private const val OBSERVER_WORK_NAME = "ScreenshotObserverWork"

    fun scheduleSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build()

        val request = OneTimeWorkRequestBuilder<ScreenshotSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleObserver(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val constraints = Constraints.Builder()
                .addContentUriTrigger(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true)
                .build()

            val request = OneTimeWorkRequestBuilder<ScreenshotSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                OBSERVER_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
