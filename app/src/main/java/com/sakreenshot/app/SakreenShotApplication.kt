package com.sakreenshot.app

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.sakreenshot.app.data.db.AppDatabase
import com.sakreenshot.app.data.repository.DataRepository
import com.sakreenshot.app.worker.WorkManagerHelper

class SakreenShotApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { DataRepository(database.screenshotDao()) }

    override fun onCreate() {
        super.onCreate()
        
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            WorkManagerHelper.scheduleObserver(this)
        }
    }
}
