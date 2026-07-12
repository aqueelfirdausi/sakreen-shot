package com.sakreenshot.app.data.db

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = ScreenshotEntity::class)
@Entity(tableName = "screenshots_fts")
data class ScreenshotFtsEntity(
    val normalizedText: String
)
