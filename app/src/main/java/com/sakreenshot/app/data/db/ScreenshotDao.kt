package com.sakreenshot.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(screenshot: ScreenshotEntity): Long

    @Query("SELECT * FROM screenshots ORDER BY capturedAt DESC")
    fun observeAll(): Flow<List<ScreenshotEntity>>

    @Query("SELECT primaryCategory, COUNT(*) as count FROM screenshots GROUP BY primaryCategory")
    fun observeCategoryCounts(): Flow<List<CategoryCount>>

    @Query("""
        SELECT * FROM screenshots 
        WHERE extractedText LIKE '%' || :query || '%' 
           OR normalizedText LIKE '%' || :query || '%'
           OR displayName LIKE '%' || :query || '%'
           OR primaryCategory LIKE '%' || :query || '%'
        ORDER BY capturedAt DESC
    """)
    fun searchExtractedText(query: String): Flow<List<ScreenshotEntity>>

    @Query("SELECT * FROM screenshots WHERE primaryCategory = :category ORDER BY capturedAt DESC")
    fun observeByCategory(category: String): Flow<List<ScreenshotEntity>>

    @Query("SELECT * FROM screenshots WHERE isPinned = 1 ORDER BY capturedAt DESC")
    fun observePinned(): Flow<List<ScreenshotEntity>>

    @Query("UPDATE screenshots SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinState(id: Long, isPinned: Boolean): Unit

    @Query("UPDATE screenshots SET primaryCategory = :category WHERE id = :id")
    suspend fun updateCategory(id: Long, category: String): Unit

    @Delete
    suspend fun delete(screenshot: ScreenshotEntity): Unit

    @Query("DELETE FROM screenshots WHERE mediaStoreId IN (:mediaStoreIds)")
    suspend fun deleteByMediaStoreIds(mediaStoreIds: List<Long>): Unit

    @Query("UPDATE screenshots SET processingStatus = 'MISSING_MEDIA' WHERE mediaStoreId = :mediaStoreId")
    suspend fun markMissingMedia(mediaStoreId: Long): Unit

    @Query("SELECT * FROM screenshots WHERE mediaStoreId = :mediaStoreId LIMIT 1")
    suspend fun findByMediaStoreId(mediaStoreId: Long): ScreenshotEntity?

    @Query("SELECT mediaStoreId FROM screenshots")
    suspend fun getAllMediaStoreIds(): List<Long>

    @Query("SELECT * FROM screenshots WHERE capturedAt < :thresholdTime OR estimatedExpiry < :currentTime OR primaryCategory = 'UNSORTED' ORDER BY capturedAt ASC")
    suspend fun fetchCleanupCandidates(thresholdTime: Long, currentTime: Long): List<ScreenshotEntity>
}

data class CategoryCount(
    val primaryCategory: String,
    val count: Int
)
