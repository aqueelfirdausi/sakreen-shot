package com.sakreenshot.app.data.repository

import com.sakreenshot.app.data.db.ScreenshotDao
import com.sakreenshot.app.data.db.ScreenshotEntity
import kotlinx.coroutines.flow.Flow

class DataRepository(private val dao: ScreenshotDao) {

    fun observeAll(): Flow<List<ScreenshotEntity>> = dao.observeAll()

    fun observeCategoryCounts() = dao.observeCategoryCounts()

    fun searchExtractedText(query: String) = dao.searchExtractedText("*$query*") // FTS syntax

    fun observeByCategory(category: String) = dao.observeByCategory(category)

    fun observePinned() = dao.observePinned()

    suspend fun updatePinState(id: Long, isPinned: Boolean) = dao.updatePinState(id, isPinned)

    suspend fun updateCategory(id: Long, category: String) = dao.updateCategory(id, category)

    suspend fun delete(screenshot: ScreenshotEntity) = dao.delete(screenshot)

    suspend fun deleteByMediaStoreIds(mediaStoreIds: List<Long>) = dao.deleteByMediaStoreIds(mediaStoreIds)

    suspend fun markMissingMedia(mediaStoreId: Long) = dao.markMissingMedia(mediaStoreId)

    suspend fun findByMediaStoreId(mediaStoreId: Long) = dao.findByMediaStoreId(mediaStoreId)

    suspend fun fetchCleanupCandidates(thresholdTime: Long, currentTime: Long) = 
        dao.fetchCleanupCandidates(thresholdTime, currentTime)

    suspend fun insertOrUpdate(screenshot: ScreenshotEntity) = dao.insertOrUpdate(screenshot)
}
