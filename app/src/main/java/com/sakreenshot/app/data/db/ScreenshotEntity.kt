package com.sakreenshot.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenshots")
data class ScreenshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contentUri: String,
    val mediaStoreId: Long,
    val displayName: String,
    val relativePath: String?,
    val extractedText: String,
    val normalizedText: String,
    val primaryCategory: String,
    val classificationScore: Int,
    val capturedAt: Long,
    val indexedAt: Long,
    val modifiedAt: Long,
    val width: Int?,
    val height: Int?,
    val fileSize: Long?,
    val isPinned: Boolean,
    val estimatedExpiry: Long?,
    val processingStatus: String,
    val contentHash: String?
)
